package com.assu.server.domain.notification.service;

import com.assu.server.domain.common.enums.UserRole;
import com.assu.server.domain.member.entity.Member;
import com.assu.server.domain.member.repository.MemberRepository;
import com.assu.server.domain.notification.converter.NotificationConverter;
import com.assu.server.domain.notification.dto.NotificationResponseDTO;
import com.assu.server.domain.notification.dto.NotificationSettingsResponse;
import com.assu.server.domain.notification.entity.Notification;
import com.assu.server.domain.notification.entity.NotificationSetting;
import com.assu.server.domain.notification.entity.NotificationType;
import com.assu.server.domain.notification.repository.NotificationRepository;
import com.assu.server.domain.notification.repository.NotificationSettingRepository;
import com.assu.server.global.apiPayload.code.status.ErrorStatus;
import com.assu.server.global.exception.DatabaseException;
import com.assu.server.global.exception.GeneralException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class NotificationQueryServiceImpl implements NotificationQueryService {
    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;
    private final NotificationSettingRepository notificationSettingRepository;

    @Transactional
    @Override
    public Map<String, Object> getNotifications(String status, int page, int size, Long memberId) {
        // 1) 파라미터 검증
        if (page < 1) throw new DatabaseException(ErrorStatus.PAGE_UNDER_ONE);
        if (size < 1 || size > 200) throw new DatabaseException(ErrorStatus.PAGE_SIZE_INVALID);

        if (!memberRepository.existsById(memberId)) {
            throw new DatabaseException(ErrorStatus.NO_SUCH_MEMBER);
        }

        String s = (status == null ? "all" : status.toLowerCase());
        if (!s.equals("all") && !s.equals("unread")) {
            throw new DatabaseException(ErrorStatus.INVALID_NOTIFICATION_STATUS_FILTER);
        }

        // 2) 조회
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Notification> rawPage = s.equals("unread")
                ? notificationRepository.findByReceiverIdAndIsReadFalseAndTypeNot(memberId, NotificationType.CHAT, pageable)
                : notificationRepository.findByReceiverIdAndTypeNot(memberId, NotificationType.CHAT, pageable);

        Page<NotificationResponseDTO> p = rawPage.map(NotificationConverter::toDto);

        // 3) 응답 포맷
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("items", p.getContent());
        body.put("page", p.getNumber() + 1);
        body.put("size", p.getSize());
        body.put("totalPages", p.getTotalPages());
        body.put("totalElements", p.getTotalElements());
        return body;
    }

    @Override
    public NotificationSettingsResponse loadSettings(Long memberId) {
        Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NO_SUCH_MEMBER));

        // 역할별로 노출할 타입 고정
        Set<NotificationType> visible = member.getRole() == UserRole.ADMIN
                ? EnumSet.of(NotificationType.CHAT, NotificationType.PARTNER_SUGGESTION, NotificationType.PARTNER_PROPOSAL)
                : EnumSet.of(NotificationType.CHAT, NotificationType.ORDER);

        // 기본 true로 채워두고, DB 값으로 덮어쓰기
        Map<String, Boolean> map = new LinkedHashMap<>();
        for (NotificationType t : visible) map.put(t.name(), true);

        for (NotificationSetting s : notificationSettingRepository.findAllByMemberId(memberId)) {
            if (visible.contains(s.getType())) {
                map.put(s.getType().name(), Boolean.TRUE.equals(s.getEnabled()));
            }
        }
        return new NotificationSettingsResponse(map);
    }

    @Override
    public boolean hasUnread(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new DatabaseException(ErrorStatus.NO_SUCH_MEMBER);
        }
        return notificationRepository.existsByReceiverIdAndIsReadFalseAndTypeNot(memberId, NotificationType.CHAT);
    }
}
