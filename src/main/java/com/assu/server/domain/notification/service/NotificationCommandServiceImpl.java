package com.assu.server.domain.notification.service;

import com.assu.server.domain.common.entity.Member;
import com.assu.server.domain.common.repository.MemberRepository;
import com.assu.server.domain.notification.dto.QueueNotificationRequest;
import com.assu.server.domain.notification.entity.Notification;
import com.assu.server.domain.notification.entity.NotificationOutbox;
import com.assu.server.domain.notification.entity.NotificationType;
import com.assu.server.domain.notification.repository.NotificationOutboxRepository;
import com.assu.server.domain.notification.repository.NotificationRepository;
import com.assu.server.global.apiPayload.code.status.ErrorStatus;
import com.assu.server.global.exception.exception.DatabaseException;
import com.assu.server.infra.NotificationFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class NotificationCommandServiceImpl implements NotificationCommandService {
    private final NotificationRepository notificationRepository;
    private final NotificationOutboxRepository outboxRepository;
    private final NotificationFactory notificationFactory;
    private final MemberRepository memberRepository;

    @Transactional
    @Override
    public Notification createAndQueue(Long receiverId, NotificationType type, Long refId, Map<String, Object> ctx) {
        Member member = memberRepository.findMemberById(receiverId);
        if (member == null) {
            throw new DatabaseException(ErrorStatus.NO_SUCH_MEMBER);
        }

        Notification notification = notificationFactory.create(member, type, refId, ctx);

        notificationRepository.save(notification);
        outboxRepository.save(NotificationOutbox.builder()
                .notification(notification)
                .status(NotificationOutbox.Status.PENDING)
                .retryCount(0)
                .build());
        return notification;
    }

    @Transactional
    @Override
    public void markRead(Long notificationId, Long currentMemberId) {
        Notification n = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new DatabaseException(ErrorStatus.NOTIFICATION_NOT_FOUND));

        if (!n.getReceiver().getId().equals(currentMemberId)) {
            throw new DatabaseException(ErrorStatus.NOTIFICATION_ACCESS_DENIED);
        }
        n.markRead();
    }

    @Transactional
    @Override
    public void queue(QueueNotificationRequest req) {
        NotificationType type;
        try {
            type = NotificationType.valueOf(req.getType().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new DatabaseException(ErrorStatus.INVALID_NOTIFICATION_TYPE);
        }

        Map<String, Object> ctx = new HashMap<>();
        if (req.getContent()  != null) ctx.put("content",  req.getContent());
        if (req.getTitle()    != null) ctx.put("title",    req.getTitle());
        if (req.getDeeplink() != null) ctx.put("deeplink", req.getDeeplink());

        Long refId = req.getRefId();

        switch (type) {
            case CHAT -> {
                if (refId == null && req.getRoomId() == null) {
                    throw new DatabaseException(ErrorStatus.MISSING_NOTIFICATION_FIELD);
                }
                refId = (refId != null) ? refId : req.getRoomId();
                if (req.getSenderName() == null || req.getMessage() == null) {
                    throw new DatabaseException(ErrorStatus.MISSING_NOTIFICATION_FIELD);
                }
                ctx.put("senderName", req.getSenderName());
                ctx.put("message", req.getMessage());
            }
            case PARTNER_SUGGESTION -> {
                if (refId == null && req.getSuggestionId() == null) {
                    throw new DatabaseException(ErrorStatus.MISSING_NOTIFICATION_FIELD);
                }
                refId = (refId != null) ? refId : req.getSuggestionId();
            }
            case ORDER -> {
                if (refId == null && req.getOrderId() == null) {
                    throw new DatabaseException(ErrorStatus.MISSING_NOTIFICATION_FIELD);
                }
                refId = (refId != null) ? refId : req.getOrderId();
                if (req.getTable_num() == null || req.getPaper_content() == null) {
                    throw new DatabaseException(ErrorStatus.MISSING_NOTIFICATION_FIELD);
                }
                ctx.put("table_num", req.getTable_num());
                ctx.put("paper_content", req.getPaper_content());
            }
            case PARTNER_PROPOSAL -> {
                if (refId == null && req.getProposalId() == null) {
                    throw new DatabaseException(ErrorStatus.MISSING_NOTIFICATION_FIELD);
                }
                refId = (refId != null) ? refId : req.getProposalId();
                if (req.getPartner_name() == null) {
                    throw new DatabaseException(ErrorStatus.MISSING_NOTIFICATION_FIELD);
                }
                ctx.put("partner_name", req.getPartner_name());
            }
            default -> throw new DatabaseException(ErrorStatus.INVALID_NOTIFICATION_TYPE);
        }

        if (req.getReceiverId() == null) {
            throw new DatabaseException(ErrorStatus.MISSING_NOTIFICATION_FIELD);
        }

        createAndQueue(req.getReceiverId(), type, refId, ctx);
    }
}
