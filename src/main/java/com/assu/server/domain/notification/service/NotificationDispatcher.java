package com.assu.server.domain.notification.service;

import com.assu.server.domain.notification.entity.Notification;
import com.assu.server.domain.notification.entity.NotificationOutbox;
import com.assu.server.domain.notification.repository.NotificationOutboxRepository;
import com.assu.server.infra.firebase.FcmClient;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class NotificationDispatcher {
    private final NotificationOutboxRepository outboxRepo;
    private final FcmClient fcmClient;

    @Scheduled(fixedDelay = 1000) // 1초 간격 배치
    @Transactional
    public void dispatch() {
        List<NotificationOutbox> batch =
                outboxRepo.findTop50ByStatusOrderByIdAsc(NotificationOutbox.Status.PENDING);

        for (NotificationOutbox o : batch) {
            try {
                Notification notification = o.getNotification();
                fcmClient.sendToMember(notification.getReceiver(), notification);
                o.markSent();
            } catch (Exception e) {
                o.incRetry();
                if (o.getRetryCount() >= 5) o.markFailed(); // 과도한 재시도 방지
            }
        }
    }
}
