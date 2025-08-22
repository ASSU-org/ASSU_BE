package com.assu.server.domain.notification.converter;

import com.assu.server.domain.notification.dto.NotificationResponseDTO;
import com.assu.server.domain.notification.entity.Notification;

import java.time.Duration;
import java.time.LocalDateTime;

public class NotificationConverter {
    public static NotificationResponseDTO toDto(Notification n) {
        return NotificationResponseDTO.builder()
                .id(n.getId())
                .type(n.getType().name())
                .refId(n.getRefId())
                .title(n.getTitle())
                .messagePreview(n.getMessagePreview())
                .deeplink(n.getDeeplink())
                .isRead(n.isRead())
                .createdAt(n.getCreatedAt())
                .readAt(n.getReadAt())
                .timeAgo(toTimeAgo(n.getCreatedAt()))
                .build();
    }

    /** 1시간 전까지 분 단위, 그 이후는 시간 단위(24h 초과 시 일 단위) */
    public static String toTimeAgo(LocalDateTime createdAt) {
        if (createdAt == null) return "";

        LocalDateTime now = LocalDateTime.now();
        if (createdAt.isAfter(now)) return "방금 전";

        Duration d = Duration.between(createdAt, now);
        long minutes = d.toMinutes();

        if (minutes < 1) return "방금 전";
        if (minutes < 60) return minutes + "분 전";

        long hours = minutes / 60;
        if (hours < 24) return hours + "시간 전";

        long days = hours / 24;
        return days + "일 전";
    }
}
