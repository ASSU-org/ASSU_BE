package com.assu.server.domain.notification.service;


import com.assu.server.domain.member.entity.Member;
import com.assu.server.domain.member.repository.MemberRepository;
import com.assu.server.domain.notification.dto.QueueNotificationRequest;
import com.assu.server.domain.notification.entity.*;
import com.assu.server.domain.notification.repository.NotificationOutboxRepository;
import com.assu.server.domain.notification.repository.NotificationRepository;
import com.assu.server.domain.notification.repository.NotificationSettingRepository;
import com.assu.server.global.apiPayload.code.status.ErrorStatus;
import com.assu.server.global.exception.DatabaseException;
import com.assu.server.global.exception.GeneralException;
import com.assu.server.infra.firebase.NotificationFactory;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
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
    private final ApplicationEventPublisher events;


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
        NotificationOutbox outbox = outboxRepository.save(
                NotificationOutbox.builder()
                        .notification(notification)
                        .status(NotificationOutbox.Status.PENDING)
                        .retryCount(0)
                        .build()
        );

        events.publishEvent(new OutboxCreatedEvent(outbox.getId(), notification));
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
        if (req.getType() == null) {
            throw new DatabaseException(ErrorStatus.INVALID_NOTIFICATION_TYPE);
        }
        if (req.getReceiverId() == null) {
            throw new DatabaseException(ErrorStatus.MISSING_NOTIFICATION_FIELD);
        }

        final NotificationType type;
        try {
            type = NotificationType.valueOf(req.getType().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new DatabaseException(ErrorStatus.INVALID_NOTIFICATION_TYPE);
        }

        final Long receiverId = req.getReceiverId();

        switch (type) {
            case CHAT -> {
                // refId 우선순위: refId > roomId
                Long roomId = (req.getRefId() != null) ? req.getRefId() : req.getRoomId();
                if (roomId == null || req.getSenderName() == null || req.getMessage() == null) {
                    throw new DatabaseException(ErrorStatus.MISSING_NOTIFICATION_FIELD);
                }
                // 퍼사드 호출: 내부에서 ON/OFF 자동 반영
                sendChat(receiverId, roomId, req.getSenderName(), req.getMessage());
            }

            case PARTNER_SUGGESTION -> {
                Long suggestionId = (req.getRefId() != null) ? req.getRefId() : req.getSuggestionId();
                if (suggestionId == null) {
                    throw new DatabaseException(ErrorStatus.MISSING_NOTIFICATION_FIELD);
                }
                sendPartnerSuggestion(receiverId, suggestionId);
            }

            case ORDER -> {
                Long orderId = (req.getRefId() != null) ? req.getRefId() : req.getOrderId();
                if (orderId == null || req.getTable_num() == null || req.getPaper_content() == null) {
                    throw new DatabaseException(ErrorStatus.MISSING_NOTIFICATION_FIELD);
                }
                sendOrder(receiverId, orderId, req.getTable_num(), req.getPaper_content());
            }

            case PARTNER_PROPOSAL -> {
                Long proposalId = (req.getRefId() != null) ? req.getRefId() : req.getProposalId();
                if (proposalId == null || req.getPartner_name() == null) {
                    throw new DatabaseException(ErrorStatus.MISSING_NOTIFICATION_FIELD);
                }
                sendPartnerProposal(receiverId, proposalId, req.getPartner_name());
            }

            default -> throw new DatabaseException(ErrorStatus.INVALID_NOTIFICATION_TYPE);
        }
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


    @Transactional
    protected void sendIfEnabled(Long receiverId, NotificationType type, Long refId, Map<String, Object> ctx) {
        // OFF면 기록만 남기고 종료, ON이면 Outbox 적재
        if (!isEnabled(receiverId, type)) {
            Member member = memberRepository.findMemberById(receiverId)
                    .orElseThrow(() -> new GeneralException(ErrorStatus.NO_SUCH_MEMBER));
            notificationRepository.save(notificationFactory.create(member, type, refId, ctx));
            return;
        }
        createAndQueue(receiverId, type, refId, ctx);
    }

    @Transactional
    @Override
    public void sendChat(Long receiverId, Long roomId, String senderName, String message) {
        if (receiverId == null || roomId == null || senderName == null || message == null) {
            throw new DatabaseException(ErrorStatus.MISSING_NOTIFICATION_FIELD);
        }
        sendIfEnabled(
                receiverId,
                NotificationType.CHAT,
                roomId, // Factory가 /chat/rooms/{refId}로 딥링크 생성
                Map.of(
                        "senderName", senderName,     // Factory가 title/preview 생성에 사용
                        "message", message            // Factory가 미리보기 생성에 사용
                )
        );
    }

    @Transactional
    @Override
    public void sendPartnerSuggestion(Long receiverId, Long suggestionId) {
        if (receiverId == null || suggestionId == null) {
            throw new DatabaseException(ErrorStatus.MISSING_NOTIFICATION_FIELD);
        }
        sendIfEnabled(
                receiverId,
                NotificationType.PARTNER_SUGGESTION,
                suggestionId,                    // /partner/suggestions/{refId}
                Map.of()                         // 추가 ctx 없음
        );
    }

    @Transactional
    @Override
    public void sendOrder(Long receiverId, Long orderId, String tableNum, String paperContent) {
        if (receiverId == null || orderId == null || tableNum == null || paperContent == null) {
            throw new DatabaseException(ErrorStatus.MISSING_NOTIFICATION_FIELD);
        }
        sendIfEnabled(
                receiverId,
                NotificationType.ORDER,
                orderId,                         // /orders/{refId}
                Map.of(
                        "table_num", tableNum,       // Factory preview: "{table_num}번 테이블..."
                        "paper_content", paperContent
                )
        );
    }

    @Transactional
    @Override
    public void sendPartnerProposal(Long receiverId, Long proposalId, String partnerName) {
        if (receiverId == null || proposalId == null || partnerName == null) {
            throw new DatabaseException(ErrorStatus.MISSING_NOTIFICATION_FIELD);
        }
        sendIfEnabled(
                receiverId,
                NotificationType.PARTNER_PROPOSAL,
                proposalId,                      // /partner/proposals/{refId}
                Map.of(
                        "partner_name", partnerName  // Factory preview: "{partner_name}에서..."
                )
        );
    }
}
