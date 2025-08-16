package com.assu.server.domain.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QueueNotificationRequest {

    @Schema(description = "알림을 받을 멤버 ID", example = "1")
    @NotNull
    private Long receiverId;

    @Schema(description = "알림 타입 (CHAT, PARTNER_SUGGESTION, ORDER, PARTNER_PROPOSAL)", example = "CHAT")
    @NotNull
    private String type;

    // 공통(선택)
    @Schema(description = "알림 내용", example = "새로운 메시지가 있습니다.")
    private String content;

    @Schema(description = "알림 제목", example = "채팅 알림")
    private String title;

    @Schema(description = "앱 내 이동할 경로 (deeplink)", example = "app://chat/10")
    private String deeplink;

    // CHAT
    @Schema(description = "채팅방 ID", example = "101")
    private Long roomId;

    @Schema(description = "보낸 사람 이름", example = "홍길동")
    private String senderName;

    @Schema(description = "메시지 내용", example = "안녕하세요! 오늘 일정 확인 부탁드려요.")
    private String message;

    // PARTNER_SUGGESTION
    @Schema(description = "제휴 제안 ID", example = "2001")
    private Long suggestionId;

    // ORDER
    @Schema(description = "주문 ID", example = "3001")
    private Long orderId;

    @Schema(description = "테이블 번호", example = "11")
    private String table_num;

    @Schema(description = "전단지 내용", example = "20,000원 이상 구매 시 10% 할인")
    private String paper_content;

    // PARTNER_PROPOSAL
    @Schema(description = "제휴 제안 ID", example = "4001")
    private Long proposalId;

    @Schema(description = "파트너 이름", example = "역전할머니맥주 송신대점")
    private String partner_name;

    // 기타 타입 공용으로 쓰고 싶으면 유지
    @Schema(description = "참조 ID (타입별로 roomId/orderId 등 대신 사용할 수 있음)", example = "9999")
    private Long refId;
}