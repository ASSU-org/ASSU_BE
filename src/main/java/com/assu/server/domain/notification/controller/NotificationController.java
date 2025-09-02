package com.assu.server.domain.notification.controller;

import com.assu.server.domain.notification.dto.*;
import com.assu.server.domain.notification.entity.NotificationType;
import com.assu.server.domain.notification.service.NotificationCommandService;
import com.assu.server.domain.notification.service.NotificationQueryService;
import com.assu.server.global.apiPayload.BaseResponse;
import com.assu.server.global.apiPayload.code.status.SuccessStatus;
import com.assu.server.global.util.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "Notification", description = "알림 API")
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationQueryService query;
    private final NotificationCommandService command;

    @Operation(
            summary = "알림 목록 조회 API",
            description = "[v1.0 (2025-09-02)](https://www.notion.so/2491197c19ed8091b349ef0ef4bb0f60?source=copy_link) 본인의 알림 목록을 상태별로 조회합니다.\n"+
                    "- status: Request Param, String, all/unread\n" +
                    "- page: Request Param, Integer, 1 이상\n" +
                    "- size: Request Param, Integer, default = 20"
    )
    @GetMapping
    public BaseResponse<Map<String, Object>> list(
            @AuthenticationPrincipal PrincipalDetails pd,
            @RequestParam(defaultValue = "all") String status,   // all | unread
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        Map<String, Object> body = query.getNotifications(status, page, size, pd.getMemberId());
        return BaseResponse.onSuccess(SuccessStatus._OK, body);
    }

    @Operation(
            summary = "알림 읽음 처리 API",
            description = "[v1.0 (2025-09-02)](https://www.notion.so/2491197c19ed80a89ff0c03bc150460f?source=copy_link) 알림 아이디에 해당하는 알림을 읽음 처리합니다.\n"+
                    "- notification-id: Path Variable, Long\n"
    )
    @PostMapping("/{notification-id}/read")
    public BaseResponse<String> markRead(@AuthenticationPrincipal PrincipalDetails pd,
                                         @PathVariable("notification-id") Long notificationId) throws AccessDeniedException {
        command.markRead(notificationId, pd.getMemberId());
        return BaseResponse.onSuccess(SuccessStatus._OK,
                "The notification has been marked as read successfully. id=" + notificationId);
    }

    @Operation(
            summary = "알림 전송 테스트 API",
            description = "[v1.0 (2025-09-02)](https://www.notion.so/2511197c19ed8051bc93d95f0b216543?source=copy_link) deviceToken을 등록한 이후에 사용 가능합니다."
    )
    @PostMapping("/queue")
    public BaseResponse<String> queue(@Valid @RequestBody QueueNotificationRequest req) {
        command.queue(req);
        return BaseResponse.onSuccess(SuccessStatus._OK, "Notification delivery succeeded.");
    }

    @Operation(summary = "알림 유형별 ON/OFF 토글 API",
            description = "[v1.0 (2025-09-02)](https://www.notion.so/on-off-2511197c19ed80aeb4eed3c502691361?source=copy_link) 토글 형식으로 유형별 알림을 ON/OFF 합니다.\n"+
                    "- type: Path Variable, NotificationType [CHAT / PARTNER_SUGGESTION / PARTNER_PROPOSAL / ORDER]\n")
    @PutMapping("/{type}")
    public BaseResponse<String> toggle(@AuthenticationPrincipal PrincipalDetails pd,
                                       @PathVariable("type") NotificationType type) {
        boolean newValue = command.toggle(pd.getMemberId(), type);
        return BaseResponse.onSuccess(SuccessStatus._OK,
                "Notification setting toggled: now enabled=" + newValue);
    }
}