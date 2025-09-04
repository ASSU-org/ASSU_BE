package com.assu.server.domain.notification.service;


import com.assu.server.domain.notification.dto.NotificationMessageDTO;
import com.assu.server.domain.notification.entity.OutboxCreatedEvent;
import com.assu.server.infra.firebase.AmqpConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxAfterCommitPublisher {
    private final RabbitTemplate rabbit;
    private final OutboxStatusService outboxStatus; // ← 여기!

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOutboxCreated(OutboxCreatedEvent e) {
        var n = e.getNotification();

        var dto = NotificationMessageDTO.builder()
                .idempotencyKey(String.valueOf(e.getOutboxId()))
                .receiverId(n.getReceiver().getId())
                .title(n.getTitle())
                .body(n.getMessagePreview())
                .data(Map.of(
                        "type", n.getType().name(),
                        "refId", String.valueOf(n.getRefId()),
                        "deeplink", n.getDeeplink() == null ? "" : n.getDeeplink(),
                        "notificationId", String.valueOf(n.getId())
                ))
                .build();

        rabbit.convertAndSend(AmqpConfig.EXCHANGE, AmqpConfig.ROUTING_KEY, dto);

        // ★ 새 트랜잭션에서 상태 전이
        outboxStatus.markDispatched(e.getOutboxId());
    }
}
