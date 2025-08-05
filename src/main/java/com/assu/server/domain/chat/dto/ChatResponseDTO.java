package com.assu.server.domain.chat.dto;

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
    }

    // 메시지 전송
    @Builder
    public record SendMessageResponseDTO(
        Long roomId,
        Long senderId,
        String message,
        LocalDateTime sentAt
    ) {}

    // 메시지 읽음 처리
    public record ReadMessageResponseDTO(
        Long roomId,
        int readCount
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

