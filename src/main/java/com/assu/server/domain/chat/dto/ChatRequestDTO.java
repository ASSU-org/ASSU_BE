package com.assu.server.domain.chat.dto;

import com.assu.server.domain.chat.entity.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class ChatRequestDTO {
    @Getter
    public static class CreateChatRoomRequestDTO {
        private Long adminId;
        private Long partnerId;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChatMessageRequestDTO {
        private Long roomId;
        private Long senderId;
        private Long receiverId;
        private String message;
        private int unreadCountForSender;
    }
}