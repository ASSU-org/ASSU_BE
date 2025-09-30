package com.assu.server.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomListResultDTO {
    private Long roomId;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private Long unreadMessagesCount;
    private Long opponentId;
    private String opponentName;
    private String opponentProfileImage;
    private String phoneNumber;
}
