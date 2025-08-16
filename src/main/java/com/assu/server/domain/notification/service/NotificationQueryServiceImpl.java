package com.assu.server.domain.notification.service;

import com.assu.server.domain.notification.converter.NotificationConverter;
import com.assu.server.domain.notification.dto.NotificationResponseDTO;
import com.assu.server.domain.notification.entity.Notification;
import com.assu.server.domain.notification.repository.NotificationRepository;
import com.assu.server.global.apiPayload.code.status.ErrorStatus;
import com.assu.server.global.exception.exception.DatabaseException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class NotificationQueryServiceImpl implements NotificationQueryService {
    private final NotificationRepository notificationRepository;

    @Transactional
    @Override
    public Map<String, Object> getNotifications(String status, int page, int size, Long memberId) {
        // 입력 검증
        if (page < 1)  throw new DatabaseException(ErrorStatus.PAGE_UNDER_ONE);
        if (size < 1 || size > 200) throw new DatabaseException(ErrorStatus.PAGE_SIZE_INVALID);

        String s = status == null ? "all" : status.toLowerCase();
        if (!s.equals("all") && !s.equals("unread")) {
            // 필요 시 ErrorStatus에 INVALID_NOTIFICATION_STATUS_FILTER 추가해서 사용 가능
            throw new IllegalArgumentException("status must be one of [all, unread]");
        }

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "id"));

        // 상태별 조회 (여기서 바로 분기하고 변환까지)
        Page<Notification> rawPage = s.equals("unread")
                ? notificationRepository.findByReceiverIdAndIsReadFalse(memberId, pageable)
                : notificationRepository.findByReceiverId(memberId, pageable);

        Page<NotificationResponseDTO> p = rawPage.map(NotificationConverter::toDto);

        // 응답 포맷 구성
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("items", p.getContent());
        body.put("page", p.getNumber() + 1);      // 1-base로 반환
        body.put("size", p.getSize());
        body.put("totalPages", p.getTotalPages());
        body.put("totalElements", p.getTotalElements());
        return body;
    }
}
