package com.assu.server.domain.chat.dto;

import com.assu.server.domain.chat.entity.enums.MessageType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.protobuf.Enum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

public class ChatResponseDTO {

    // 채팅방 목록 조회
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateChatRoomResponseDTO {
        private Long roomId;
        private String adminViewName;
        private String partnerViewName;
    }

    // 메시지 전송
    @Builder
    public record SendMessageResponseDTO(
        Long messageId,
        Long roomId,
        Long senderId,
        Long receiverId,
        String message,
        MessageType messageType,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime sentAt
    ) {}

    // 메시지 읽음 처리
    public record ReadMessageResponseDTO(
        Long roomId,
        Long readerId,
        List<Long> readMessagesId,
        int readCount,
        boolean isRead
    ) {}

    // 채팅방 들어갔을 때 조회
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ChatHistoryResponseDTO {
        private Long roomId;
        private List<ChatMessageDTO> messages;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LeaveChattingRoomResponseDTO {
        private Long roomId;
        private boolean isLeftSuccessfully;
        private boolean isRoomDeleted;
    }
}

