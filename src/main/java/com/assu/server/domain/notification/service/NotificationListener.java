package com.assu.server.domain.notification.service;

import com.assu.server.infra.firebase.AmqpConfig;
import com.assu.server.infra.firebase.FcmClient;
import com.assu.server.domain.notification.dto.NotificationMessageDTO;

import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationListener {

    private final FcmClient fcmClient;
    private final OutboxStatusService outboxStatus; // ← 주입

    @RabbitListener(queues = AmqpConfig.QUEUE)
    public void onMessage(@Payload NotificationMessageDTO payload,
                          Channel ch,
                          @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws Exception {
        try {
            fcmClient.sendToMemberId(
                    payload.getReceiverId(),
                    payload.getTitle(),
                    payload.getBody(),
                    payload.getData()
            );
            ch.basicAck(tag, false);

            // idempotencyKey = outboxId 로 보냈으니 그대로 사용
            Long outboxId = Long.valueOf(payload.getIdempotencyKey());
            outboxStatus.markSent(outboxId); // 새 트랜잭션에서 SENT 전이
        } catch (RuntimeException e) {
            if (isTransient(e)) {
                ch.basicNack(tag, false, true);
            } else {
                ch.basicNack(tag, false, false);
            }
        } catch (Exception e) {
            ch.basicNack(tag, false, false);
        }
    }

    private boolean isTransient(Throwable t) {
        while (t != null) {
            if (t instanceof java.util.concurrent.TimeoutException
                    || t instanceof java.net.SocketTimeoutException
                    || t instanceof java.io.IOException) {
                return true;
            }
            t = t.getCause();
        }
        return false;
    }
}

