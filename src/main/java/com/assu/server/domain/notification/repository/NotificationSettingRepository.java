package com.assu.server.domain.notification.repository;

import com.assu.server.domain.notification.entity.NotificationSetting;
import com.assu.server.domain.notification.entity.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationSettingRepository extends JpaRepository<NotificationSetting, Long> {
    Optional<NotificationSetting> findByMemberIdAndType(Long memberId, NotificationType type);
    boolean existsByMemberIdAndTypeAndEnabledTrue(Long memberId, NotificationType type);
}
