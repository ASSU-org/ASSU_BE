package com.assu.server.domain.notification.controller;

import com.assu.server.domain.notification.dto.*;
import com.assu.server.domain.notification.entity.NotificationType;
import com.assu.server.domain.notification.service.NotificationCommandService;
import com.assu.server.domain.notification.service.NotificationQueryService;
import com.assu.server.global.apiPayload.BaseResponse;
import com.assu.server.global.apiPayload.code.status.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationQueryService query;
    private final NotificationCommandService command;

    @Operation(
            summary = "알림 목록 조회 API",
            description = "page는 1 이상이어야 합니다."
    )
    @GetMapping
    public BaseResponse<Map<String, Object>> list(
            @RequestParam(defaultValue = "all") String status,   // all | unread
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam Long memberId
    ) {
        Map<String, Object> body = query.getNotifications(status, page, size, memberId);
        return BaseResponse.onSuccess(SuccessStatus._OK, body);
    }


    @Operation(
            summary = "알림 읽음 처리 API",
            description = "알림 아이디를 보내주세요"
    )
    @PostMapping("/{notificationId}/read")
    public BaseResponse<String> markRead(@PathVariable Long notificationId,
                         @RequestParam Long memberId) throws AccessDeniedException {
        command.markRead(notificationId, memberId);
        return BaseResponse.onSuccess(SuccessStatus._OK,"The notification has been marked as read successfully." + notificationId);
    }

    @Operation(
            summary = "알림 전송 테스트 API",
            description = "API 명세서의 [notification > 알림 보내기 테스트] 페이지의 예시 request를 참고해서 테스트 해주세요!"+
                    "deviceToken을 등록하신 이후에 확인 가능합니다."
    )
    @PostMapping("/queue")
    public BaseResponse<String> queue(@Valid @RequestBody QueueNotificationRequest req) {
        command.queue(req);
        return BaseResponse.onSuccess(SuccessStatus._OK, "Notification delivery succeeded.");
    }

    @Operation(summary = "알림 유형별 ON/OFF 토글 API")
    @PutMapping("/{memberId}/{type}/toggle")
    public BaseResponse<String> toggle(@PathVariable Long memberId,
                                       @PathVariable NotificationType type) {
        boolean newValue = command.toggle(memberId, type);
        return BaseResponse.onSuccess(SuccessStatus._OK,
                "Notification setting toggled: now enabled=" + newValue);
    }

}
