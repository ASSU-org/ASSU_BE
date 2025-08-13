package com.assu.server.domain.notification.controller;

import com.assu.server.domain.notification.dto.NotificationResponseDTO;
import com.assu.server.domain.notification.service.NotificationCommandService;
import com.assu.server.domain.notification.service.NotificationQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import java.nio.file.AccessDeniedException;

@RestController
@RequestMapping("notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationQueryService query;
    private final NotificationCommandService command;

    @GetMapping
    public Page<NotificationResponseDTO> list(
            @RequestParam(defaultValue = "all") String status,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam Long memberId) {

        if (!"all".equalsIgnoreCase(status) && !"unread".equalsIgnoreCase(status)) {
            throw new IllegalArgumentException("status must be one of [all, unread]");
        }
        return query.listByStatus(status, pageable, memberId);
    }


    @PostMapping("/{notification_id}/read")
    public void markRead(@PathVariable Long id,
                         @RequestParam Long memberId) throws AccessDeniedException {
        command.markRead(id, memberId);
    }U
}
