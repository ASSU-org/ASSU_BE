package com.assu.server.domain.notification.service;

import com.assu.server.domain.notification.dto.QueueNotificationRequest;
import com.assu.server.domain.notification.entity.Notification;
import com.assu.server.domain.notification.entity.NotificationType;

import java.nio.file.AccessDeniedException;
import java.util.Map;

public interface NotificationCommandService {
    Notification createAndQueue(Long receiverId, NotificationType type, Long refId, Map<String, Object> ctx);
    void markRead(Long notificationId, Long currentMemberId) throws AccessDeniedException;
    void queue(QueueNotificationRequest req);
    boolean toggle(Long memberId, NotificationType type);
    boolean isEnabled(Long memberId, NotificationType type);
}
