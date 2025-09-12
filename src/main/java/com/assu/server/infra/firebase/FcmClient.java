package com.assu.server.infra.firebase;

import com.assu.server.domain.deviceToken.repository.DeviceTokenRepository;
import com.google.api.core.ApiFuture;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
@RequiredArgsConstructor
public class FcmClient {
    private final FirebaseMessaging messaging;
    private final DeviceTokenRepository tokenRepo;

    // 전송 타임아웃
    private static final Duration SEND_TIMEOUT = Duration.ofSeconds(3);

    public void sendToMemberId(Long memberId, String title, String body, Map<String, String> data) {
        if (memberId == null) {
            throw new IllegalArgumentException("receiverId is null");
        }

        // 1) 토큰 조회
        List<String> tokens = tokenRepo.findActiveTokensByMemberId(memberId);
        if (tokens.isEmpty()) return;

        // 2) 널 세이프 처리
        final String _title = title == null ? "" : title;
        final String _body  = body  == null ? "" : body;

        String type           = (data != null && data.get("type") != null) ? data.get("type") : "";
        String refId          = (data != null && data.get("refId") != null) ? data.get("refId") : "";
        String deeplink       = (data != null && data.get("deeplink") != null) ? data.get("deeplink") : "";
        String notificationId = (data != null && data.get("notificationId") != null) ? data.get("notificationId") : "";

        // 3) 각 토큰에 FCM 전송 (data-only + HIGH)
        for (String token : tokens) {
            Message msg = Message.builder()
                    .setToken(token)
                    .setAndroidConfig(AndroidConfig.builder()
                            .setPriority(AndroidConfig.Priority.HIGH)
                            // 필요 시 TTL 등 추가 가능
                            // .setTtl(10_000L) // 10초
                            .build())
                    // --- data-only payload ---
                    .putData("title", _title)
                    .putData("body",  _body)
                    .putData("type",  type)
                    .putData("refId", refId)
                    .putData("deeplink", deeplink)
                    .putData("notificationId", notificationId)
                    .build();

            try {
                ApiFuture<String> future = messaging.sendAsync(msg);
                String messageId = future.get(SEND_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);
                log.debug("[FCM] sent messageId={} memberId={}", messageId, memberId);
            } catch (TimeoutException te) {
                log.warn("[FCM] timeout ({} ms) memberId={}", SEND_TIMEOUT.toMillis(), memberId);
                throw new RuntimeException("FCM timeout", te);
            } catch (Exception e) {
                throw new RuntimeException("FCM unexpected error", e);
            }
        }
    }
}