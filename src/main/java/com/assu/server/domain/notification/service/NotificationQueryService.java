package com.assu.server.domain.notification.service;

import com.assu.server.domain.notification.dto.NotificationResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface NotificationQueryService {
    Map<String, Object> getNotifications(String status, int page, int size, Long memberId);
}
