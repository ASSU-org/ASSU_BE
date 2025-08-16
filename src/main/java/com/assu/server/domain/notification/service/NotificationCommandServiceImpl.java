package com.assu.server.domain.notification.service;

import com.assu.server.domain.common.entity.Member;
import com.assu.server.domain.common.repository.MemberRepository;
import com.assu.server.domain.notification.dto.QueueNotificationRequest;
import com.assu.server.domain.notification.entity.Notification;
import com.assu.server.domain.notification.entity.NotificationOutbox;
import com.assu.server.domain.notification.entity.NotificationType;
import com.assu.server.domain.notification.repository.NotificationOutboxRepository;
import com.assu.server.domain.notification.repository.NotificationRepository;
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
    public void markRead(Long notificationId, Long currentMemberId) throws AccessDeniedException {
        Notification n = notificationRepository.findById(notificationId).orElseThrow();
        if (!n.getReceiver().getId().equals(currentMemberId)) {
            throw new AccessDeniedException("not yours");
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
            throw new IllegalArgumentException("Unsupported type: " + req.getType());
        }

        Map<String, Object> ctx = new HashMap<>();
        // 공통 컨텍스트(있으면 추가)
        if (req.getContent()  != null) ctx.put("content",  req.getContent());
        if (req.getTitle()    != null) ctx.put("title",    req.getTitle());
        if (req.getDeeplink() != null) ctx.put("deeplink", req.getDeeplink());

        Long refId = req.getRefId(); // 우선 refId가 넘어오면 사용

        switch (type) {
            case CHAT -> {
                // refId 우선순위: refId 필드 → roomId
                if (refId == null) {
                    refId = Objects.requireNonNull(req.getRoomId(), "roomId is required for CHAT");
                }
                ctx.put("senderName", Objects.requireNonNull(req.getSenderName(), "senderName is required for CHAT"));
                ctx.put("message",    Objects.requireNonNull(req.getMessage(),    "message is required for CHAT"));
            }
            case PARTNER_SUGGESTION -> {
                if (refId == null) {
                    refId = Objects.requireNonNull(req.getSuggestionId(), "suggestionId is required for PARTNER_SUGGESTION");
                }
                // 추가 ctx 없음
            }
            case ORDER -> {
                if (refId == null) {
                    refId = Objects.requireNonNull(req.getOrderId(), "orderId is required for ORDER");
                }
                ctx.put("table_num",     Objects.requireNonNull(req.getTable_num(),     "table_num is required for ORDER"));
                ctx.put("paper_content", Objects.requireNonNull(req.getPaper_content(), "paper_content is required for ORDER"));
            }
            case PARTNER_PROPOSAL -> {
                if (refId == null) {
                    refId = Objects.requireNonNull(req.getProposalId(), "proposalId is required for PARTNER_PROPOSAL");
                }
                ctx.put("partner_name", Objects.requireNonNull(req.getPartner_name(), "partner_name is required for PARTNER_PROPOSAL"));
            }
            default -> throw new IllegalArgumentException("Unsupported type: " + type);
        }

        // 최종 큐 적재 (Outbox → Dispatcher가 발송)
        createAndQueue(
                Objects.requireNonNull(req.getReceiverId(), "receiverId is required"),
                type,
                Objects.requireNonNull(refId, "refId is required"),
                ctx
        );
    }
}
