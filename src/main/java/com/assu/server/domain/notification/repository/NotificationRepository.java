package com.assu.server.domain.notification.repository;

import com.assu.server.domain.deviceToken.entity.DeviceToken;
import com.assu.server.domain.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByReceiverId(Long receiverId, Pageable pageable);
    Page<Notification> findByReceiverIdAndIsReadFalse(Long receiverId, Pageable pageable);

    @Modifying
    @Query("update Notification n set n.isRead=true, n.readAt=:now where n.receiver.id=:memberId and n.isRead=false")
    void markAllRead(@Param("memberId") Long memberId, @Param("now") LocalDateTime now);
}
