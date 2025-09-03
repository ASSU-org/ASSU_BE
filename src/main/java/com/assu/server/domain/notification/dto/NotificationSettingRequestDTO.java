package com.assu.server.domain.notification.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationSettingRequestDTO {
    @NotNull
    private Boolean enabled;
}
