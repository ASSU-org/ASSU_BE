package com.assu.server.domain.notification.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QueueNotificationRequest {
    @NotNull private Long receiverId;
    @NotNull private String type;

    // 공통(선택)
    private String content;
    private String title;
    private String deeplink;

    // CHAT
    private Long roomId;
    private String senderName;
    private String message;

    // PARTNER_SUGGESTION
    private Long suggestionId;

    // ORDER
    private Long orderId;
    private String table_num;
    private String paper_content;

    // PARTNER_PROPOSAL
    private Long proposalId;
    private String partner_name;

    // 기타 타입 공용으로 쓰고 싶으면 유지
    private Long refId; // 있으면 우선 사용 (없을 때는 타입별 필드에서 채움)
}
