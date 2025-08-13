package com.assu.server.domain.notification.service;

import com.assu.server.domain.common.entity.Member;
import com.assu.server.domain.notification.converter.NotificationConverter;
import com.assu.server.domain.notification.dto.NotificationResponseDTO;
import com.assu.server.domain.notification.entity.Notification;
import com.assu.server.domain.notification.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationQueryServiceImpl implements NotificationQueryService {
    private final NotificationRepository notificationRepository;
    //private final MemberRepository memberRepository;

    @Transactional
    @Override
    public Page<NotificationResponseDTO> listByStatus(String status, Pageable pageable, Long memberId) {
        boolean unreadOnly = "unread".equalsIgnoreCase(status);

        Page<Notification> page = unreadOnly
                ? notificationRepository.findByReceiverIdAndIsReadFalse(memberId, pageable)
                : notificationRepository.findByReceiverId(memberId, pageable);

        return page.map(NotificationConverter::toDto);
    }
}
