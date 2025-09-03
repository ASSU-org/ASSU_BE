package com.assu.server.domain.notification.service;


import com.assu.server.domain.member.entity.Member;
import com.assu.server.domain.member.repository.MemberRepository;
import com.assu.server.domain.notification.dto.QueueNotificationRequest;
import com.assu.server.domain.notification.entity.Notification;
import com.assu.server.domain.notification.entity.NotificationOutbox;
import com.assu.server.domain.notification.entity.NotificationSetting;
import com.assu.server.domain.notification.entity.NotificationType;
import com.assu.server.domain.notification.repository.NotificationOutboxRepository;
import com.assu.server.domain.notification.repository.NotificationRepository;
import com.assu.server.domain.notification.repository.NotificationSettingRepository;
import com.assu.server.global.apiPayload.code.status.ErrorStatus;
import com.assu.server.global.exception.DatabaseException;
import com.assu.server.global.exception.GeneralException;
import com.assu.server.infra.firebase.NotificationFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationCommandServiceImpl implements NotificationCommandService {
    private final NotificationRepository notificationRepository;
    private final NotificationOutboxRepository outboxRepository;
    private final NotificationSettingRepository notificationSettingRepository;
    private final NotificationFactory notificationFactory;
    private final MemberRepository memberRepository;

    @Transactional
    @Override
    public Notification createAndQueue(Long receiverId, NotificationType type, Long refId, Map<String, Object> ctx) {
        Member member = memberRepository.findMemberById(receiverId).orElseThrow(
            () -> new GeneralException(ErrorStatus.NO_SUCH_MEMBER)
        );
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

        // OFF면 Outbox 적재 없이 Notification만 저장하고 종료
        boolean enabled = isEnabled(req.getReceiverId(), type);

        if (!enabled) {
            // 기록만 남기고 발송은 스킵
            var member = memberRepository.findMemberById(req.getReceiverId()).orElseThrow(
                () -> new GeneralException(ErrorStatus.NO_SUCH_MEMBER)
            );
            var notification = notificationFactory.create(member, type, refId, ctx);
            notificationRepository.save(notification);
            return;
        }

        createAndQueue(req.getReceiverId(), type, refId, ctx);
    }

    @Transactional
    @Override
    public boolean toggle(Long memberId, NotificationType type) {

        Member member = memberRepository.findMemberById(memberId).orElseThrow(
            () -> new GeneralException(ErrorStatus.NO_SUCH_MEMBER)
        );
        NotificationSetting setting = notificationSettingRepository
                .findByMemberIdAndType(memberId, type)
                .orElse(NotificationSetting.builder()
                        .member(member)
                        .type(type)
                        .enabled(true) // 기본값
                        .build());

        setting.setEnabled(!setting.getEnabled()); // 토글
        notificationSettingRepository.save(setting);

        return setting.getEnabled(); // 변경된 값 반환
    }

    @Transactional
    @Override
    public boolean isEnabled(Long memberId, NotificationType type) {
        return notificationSettingRepository.findByMemberIdAndType(memberId, type)
                .map(ns -> Boolean.TRUE.equals(ns.getEnabled())) // null → false 처리
                .orElse(true); // 설정 없으면 기본 허용
    }
}
