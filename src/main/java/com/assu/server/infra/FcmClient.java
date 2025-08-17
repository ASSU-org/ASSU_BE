package com.assu.server.infra;

import com.assu.server.domain.auth.entity.Member;
import com.assu.server.domain.deviceToken.repository.DeviceTokenRepository;
import com.assu.server.domain.notification.entity.Notification;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FcmClient {
    private final FirebaseMessaging messaging;
    private final DeviceTokenRepository tokenRepo;

    public void sendToMember(Member receiver, Notification n) {
        List<String> tokens = tokenRepo.findActiveTokensByMemberId(receiver.getId());
        if (tokens.isEmpty()) return; // 토큰 없으면 조용히 스킵

        for (String token : tokens) {
            Message msg = Message.builder()
                    .setToken(token)
                    // notification 채널(시스템 트레이 자동 표시) + data(딥링크/타입/ID)
                    .setNotification(com.google.firebase.messaging.Notification.builder()
                            .setTitle(n.getTitle())
                            .setBody(n.getMessagePreview())
                            .build())
                    .putData("type", n.getType().name())
                    .putData("refId", String.valueOf(n.getRefId()))
                    .putData("deeplink", n.getDeeplink()==null? "" : n.getDeeplink())
                    .putData("notificationId", String.valueOf(n.getId()))
                    .build();
            try {
                messaging.send(msg);
            } catch (FirebaseMessagingException e) {
                // 실패 코드에 따라 토큰 비활성화 등 치료
                String code = e.getMessagingErrorCode() == null ? "" : e.getMessagingErrorCode().name();
                if ("UNREGISTERED".equals(code) || "INVALID_ARGUMENT".equals(code)) {
                    tokenRepo.findByToken(token).ifPresent(t -> t.setActive(false));
                }
                // 로깅/모니터링
            }
        }
    }
}
