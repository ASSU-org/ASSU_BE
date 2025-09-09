package com.assu.server.domain.notification.entity;

import com.assu.server.domain.notification.entity.Notification;
import lombok.Value;

@Value
public class OutboxCreatedEvent {
    Long outboxId;
    Notification notification;
}
