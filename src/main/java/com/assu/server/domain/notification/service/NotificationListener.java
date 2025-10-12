package com.assu.server.domain.notification.service;

import com.assu.server.infra.firebase.AmqpConfig;
import com.assu.server.infra.firebase.FcmClient;
import com.assu.server.domain.notification.dto.NotificationMessageDTO;
import com.google.firebase.messaging.FirebaseMessagingException;
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
    private final OutboxStatusService outboxStatus;

    @RabbitListener(queues = AmqpConfig.QUEUE, ackMode = "MANUAL")
    public void onMessage(@Payload NotificationMessageDTO payload,
                          Channel ch,
                          @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws Exception {

        final Long outboxId = safeParseLong(payload.getIdempotencyKey());

        try {
            // 0) Outbox 선확인: 이미 처리되었으면 중복 전송/SELECT 자체를 스킵
            if (outboxId != null && outboxStatus.isAlreadySent(outboxId)) {
                log.debug("[Notify] already-sent outboxId={}, ACK", outboxId);
                ch.basicAck(tag, false);
                return;
            }

            // 1) 전송
            FcmClient.FcmResult result = fcmClient.sendToMemberId(
                    payload.getReceiverId(), payload.getTitle(), payload.getBody(), payload.getData());

            // 2) 성공 처리
            if (outboxId != null) outboxStatus.markSent(outboxId);
            ch.basicAck(tag, false);

            // 3) 관측용 로그
            log.info("[Notify] sent outboxId={} memberId={} success={} fail={} invalidTokens={}",
                    outboxId, payload.getReceiverId(),
                    result.successCount(), result.failureCount(), result.invalidTokens());

        } catch (FirebaseMessagingException fme) {
            boolean permanent = isPermanent(fme);
            log.error("[Notify] FCM failure outboxId={} memberId={} permanent={} http={} code={} root={}",
                    outboxId, payload.getReceiverId(), permanent,
                    FcmClient.httpStatusOf(fme), fme.getMessagingErrorCode(), rootSummary(fme), fme);

            if (outboxId != null) outboxStatus.markFailed(outboxId);
            ch.basicNack(tag, false, false); // requeue 금지(지연 재시도 큐 권장)

        } catch (java.net.UnknownHostException | javax.net.ssl.SSLHandshakeException e) {
            // 환경 문제(DNS/CA)는 영구 취급(루프 방지)
            log.error("[Notify] ENV failure outboxId={} memberId={} root={}",
                    outboxId, payload.getReceiverId(), rootSummary(e), e);
            if (outboxId != null) outboxStatus.markFailed(outboxId);
            ch.basicNack(tag, false, false);

        } catch (java.util.concurrent.TimeoutException | java.net.SocketTimeoutException e) {
            // 타임아웃 → 일시 장애. 그래도 requeue(true)는 쓰지 않음
            log.warn("[Notify] TIMEOUT outboxId={} memberId={} root={}",
                    outboxId, payload.getReceiverId(), rootSummary(e), e);
            if (outboxId != null) outboxStatus.markFailed(outboxId);
            ch.basicNack(tag, false, false);

        } catch (Exception e) {
            // 알 수 없는 오류 → DLQ
            log.error("[Notify] UNKNOWN failure outboxId={} memberId={} root={}",
                    outboxId, payload.getReceiverId(), rootSummary(e), e);
            if (outboxId != null) outboxStatus.markFailed(outboxId);
            ch.basicNack(tag, false, false);
        }
    }

    private boolean isPermanent(FirebaseMessagingException fme) {
        var code = fme.getMessagingErrorCode();
        Integer http = FcmClient.httpStatusOf(fme);
        if (code == com.google.firebase.messaging.MessagingErrorCode.UNREGISTERED
                || code == com.google.firebase.messaging.MessagingErrorCode.INVALID_ARGUMENT) return true;
        if (http != null && (http == 401 || http == 403)) return true;
        return false;
    }

    private String rootSummary(Throwable t) {
        Throwable r = t; while (r.getCause() != null) r = r.getCause();
        return r.getClass().getName() + ": " + String.valueOf(r.getMessage());
    }

    private Long safeParseLong(String s) {
        try { return s == null ? null : Long.valueOf(s); } catch (Exception ignore) { return null; }
    }
}