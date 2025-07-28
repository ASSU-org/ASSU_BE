package com.assu.server.domain.chat.converter;

import com.assu.server.domain.chat.dto.ChatResponseDTO;
import com.assu.server.domain.chat.dto.ChatRoomListResultDTO;
import com.assu.server.domain.chat.entity.ChattingRoom;

import java.util.List;
import java.util.stream.Collectors;

public class ChatConverter {

    // 채팅방 리스트 아이템 하나
    public static ChatRoomListResultDTO toChatRoomResultDTO(ChatRoomListResultDTO request) {
        return ChatRoomListResultDTO.builder()
                .roomId(request.getRoomId())
                .lastMessage(request.getLastMessage())
                .lastMessageTime(request.getLastMessageTime())
                .unreadMessagesCount(request.getUnreadMessagesCount())
                .opponentId(request.getOpponentId())
                .opponentName(request.getOpponentName())
                .opponentProfileImage(request.getOpponentProfileImage())
                .build();
    }

    // 리스트 변환
    public static List<ChatRoomListResultDTO> toChatRoomListResultDTO(List<ChatRoomListResultDTO> dtos) {
        return dtos.stream()
                .map(ChatConverter::toChatRoomResultDTO)
                .collect(Collectors.toList());
    }
}
