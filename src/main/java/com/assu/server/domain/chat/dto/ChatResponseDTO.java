package com.assu.server.domain.chat.dto;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

public class ChatResponseDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateChatRoomResponseDTO {
        private Long roomId;
    }

    @Builder
    public record ChatMessageResponseDTO(
            Long roomId,
            Long senderId,
            String message,
            LocalDateTime sentAt
    ) {}

    public record ReadMessageResponseDTO(
            Long roomId,
            int readCount
    ) {}
}