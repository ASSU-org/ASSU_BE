package com.assu.server.domain.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationSettingsResponse {
    private Map<String, Boolean> settings;
}
