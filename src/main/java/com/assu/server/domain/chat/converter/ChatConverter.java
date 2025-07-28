package com.assu.server.domain.chat.converter;

import com.assu.server.domain.admin.entity.Admin;
import com.assu.server.domain.chat.dto.ChatRequestDTO;
import com.assu.server.domain.chat.dto.ChatResponseDTO;
import com.assu.server.domain.chat.dto.ChatRoomListResultDTO;
import com.assu.server.domain.chat.entity.ChattingRoom;
import com.assu.server.domain.chat.entity.Message;
import com.assu.server.domain.common.entity.Member;
import com.assu.server.domain.partner.entity.Partner;

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
    public static List<ChatRoomListResultDTO> toChatRoomListResultDTO(List<ChatRoomListResultDTO> dto) {
        return dto.stream()
                .map(ChatConverter::toChatRoomResultDTO)
                .collect(Collectors.toList());
    }

    public static ChattingRoom toCreateChattingRoom(Admin admin, Partner partner) {
        return ChattingRoom.builder()
                .admin(admin)
                .partner(partner)
                .build();
    }

    public static ChatResponseDTO.CreateChatRoomResponseDTO toCreateChatRoomIdDTO(ChattingRoom room) {
        return new ChatResponseDTO.CreateChatRoomResponseDTO(room.getId());
    }

    public static Message toMessageEntity(ChatRequestDTO.ChatMessageRequestDTO request, ChattingRoom room, Member sender) {
        return Message.builder()
                .chattingRoom(room)
                .sender(sender)
                .message(request.message())
                .build();
    }

    public static ChatResponseDTO.ChatMessageResponseDTO toChatMessageDTO(Message message) {
        return ChatResponseDTO.ChatMessageResponseDTO.builder()
                .roomId(message.getChattingRoom().getId())
                .senderId(message.getSender().getId())
                .message(message.getMessage())
                .sentAt(message.getCreatedAt())
                .build();
    }
}
