package com.assu.server.domain.notification.service;

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
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationCommandServiceImpl implements NotificationCommandService {
    private final NotificationRepository notificationRepository;
    private final NotificationOutboxRepository outboxRepository;
    private final NotificationFactory notificationFactory;

    @Transactional
    @Override
    public Notification createAndQueue(com.assu.server.domain.common.entity.Member receiver, NotificationType type, Long refId, Map<String, Object> ctx) {
        Notification notification = notificationFactory.create(receiver, type, refId, ctx);
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
}
