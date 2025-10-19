package com.assu.server.domain.chat.converter;

import com.assu.server.domain.admin.entity.Admin;
import com.assu.server.domain.chat.entity.enums.MessageType;
import com.assu.server.domain.member.entity.Member;
import com.assu.server.domain.chat.dto.ChatMessageDTO;
import com.assu.server.domain.chat.dto.ChatRequestDTO;
import com.assu.server.domain.chat.dto.ChatResponseDTO;
import com.assu.server.domain.chat.dto.ChatRoomListResultDTO;
import com.assu.server.domain.chat.entity.ChattingRoom;
import com.assu.server.domain.chat.entity.Message;

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
                .phoneNumber(request.getPhoneNumber())
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
        return ChatResponseDTO.CreateChatRoomResponseDTO.builder()
                .roomId(room.getId())
                .adminViewName(room.getPartner().getName())
                .partnerViewName(room.getAdmin().getName())
                .isNew(true)
                .build();
    }

    public static ChatResponseDTO.CreateChatRoomResponseDTO toEnterChatRoomDTO(ChattingRoom room) {
        return ChatResponseDTO.CreateChatRoomResponseDTO.builder()
                .roomId(room.getId())
                .adminViewName(room.getPartner().getName())
                .partnerViewName(room.getAdmin().getName())
                .isNew(false)
                .build();
    }

    public static Message toMessageEntity(ChatRequestDTO.ChatMessageRequestDTO request, ChattingRoom room, Member sender, Member receiver) {
        return Message.builder()
                .chattingRoom(room)
                .sender(sender)
                .receiver(receiver)
                .message(request.getMessage())
                .unreadCount(request.getUnreadCountForSender())
                .type(MessageType.TEXT)
                .build();
    }

    public static Message toGuideMessageEntity(ChatRequestDTO.ChatMessageRequestDTO request, ChattingRoom room, Member sender, Member receiver) {
        return Message.builder()
                .chattingRoom(room)
                .sender(sender)
                .receiver(receiver)
                .message(request.getMessage())
                .unreadCount(0)
                .type(MessageType.GUIDE)
                .build();
    }

    public static ChatResponseDTO.SendMessageResponseDTO toSendMessageDTO(Message message) {
        return ChatResponseDTO.SendMessageResponseDTO.builder()
                .messageId(message.getId())
                .roomId(message.getChattingRoom().getId())
                .senderId(message.getSender().getId())
                .receiverId(message.getReceiver().getId())
                .message(message.getMessage())
                .sentAt(message.getCreatedAt())
                .messageType(message.getType())
                .unreadCountForSender(message.getUnreadCount())
                .build();
    }

//    public static ChatMessageDTO toChatMessageDTO(Message message, Long currentUserId) {
//        return ChatMessageDTO.builder()
//                .messageId(message.getId())
//                .message(message.getMessage())
//                .sendTime(message.getCreatedAt())
//                .isRead(message.isRead())
//                .isMyMessage(message.getSender().getId().equals(currentUserId))
//                .build();
//    }

    public static ChatResponseDTO.ChatHistoryResponseDTO toChatHistoryDTO(
            Long roomId,
            List<ChatMessageDTO> messages) {

        // ③ 최종 DTO 빌드
        return ChatResponseDTO.ChatHistoryResponseDTO.builder()
                .roomId(roomId)
                .messages(messages)
                .build();
    }
}
