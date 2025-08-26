package com.assu.server.domain.notification.dto;

import com.assu.server.domain.notification.entity.Notification;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponseDTO {

    private Long id;
    private String type;
    private Long refId;
    private String title;
    private String messagePreview;
    private String deeplink;
    private boolean isRead;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
    private String timeAgo;
}

