package com.assu.server.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class ChatResponseDTO {
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChatRoomListResultDTO {
        private String roomId;
        private String lastMessage;
        private LocalDateTime lastMessageTime;
        private int unreadMessagesCount;
        private Opponent opponent;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Opponent {
        private Long  opponentId;
        private String opponentName;
        private String profileImageUrl;
    }
}
