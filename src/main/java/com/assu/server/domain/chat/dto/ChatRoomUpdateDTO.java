package com.assu.server.domain.chat.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ChatRoomUpdateDTO {
    private Long roomId;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private Long unreadCount; // 해당 채팅방의 총 안읽은 메시지 수
}
