package com.assu.server.domain.chat.service;

import com.assu.server.domain.chat.dto.ChatRequestDTO;
import com.assu.server.domain.chat.dto.ChatResponseDTO;
import com.assu.server.domain.chat.dto.ChatRoomListResultDTO;
import java.util.List;

public interface ChatService {
    List<ChatRoomListResultDTO> getChatRoomList();
    ChatResponseDTO.CreateChatRoomResponseDTO createChatRoom(ChatRequestDTO.CreateChatRoomRequestDTO request);
    ChatResponseDTO.SendMessageResponseDTO handleMessage(ChatRequestDTO.ChatMessageRequestDTO request);
    ChatResponseDTO.ReadMessageResponseDTO readMessage(Long roomId);
    ChatResponseDTO.ChatHistoryResponseDTO readHistory(Long roomId);
    ChatResponseDTO.LeaveChattingRoomResponseDTO leaveChattingRoom(Long roomId);
}
