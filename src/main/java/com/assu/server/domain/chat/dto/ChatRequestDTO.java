package com.assu.server.domain.chat.dto;

import lombok.Getter;

public class ChatRequestDTO {
    @Getter
    public static class CreateChatRoomRequestDTO {
        private Long adminId;
        private Long partnerId;
    }
}
