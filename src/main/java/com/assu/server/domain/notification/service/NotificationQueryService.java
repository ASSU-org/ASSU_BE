package com.assu.server.domain.notification.service;

import com.assu.server.domain.notification.dto.NotificationResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationQueryService {
    Page<NotificationResponseDTO> listByStatus(String status, Pageable pageable, Long memberId);
}
