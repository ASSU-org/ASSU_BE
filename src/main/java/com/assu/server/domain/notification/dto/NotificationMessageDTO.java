package com.assu.server.domain.notification.dto;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationMessageDTO {
    private String idempotencyKey;
    private Long receiverId;
    private String title;
    private String body;
    private Map<String, String> data;
}
