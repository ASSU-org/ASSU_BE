package com.assu.server.domain.notification.repository;

import com.assu.server.domain.notification.entity.NotificationOutbox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationOutboxRepository extends JpaRepository<NotificationOutbox, Long> {
    List<NotificationOutbox> findTop50ByStatusOrderByIdAsc(NotificationOutbox.Status status);
}
