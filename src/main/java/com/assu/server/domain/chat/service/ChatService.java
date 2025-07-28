package com.assu.server.domain.chat.service;

import com.assu.server.domain.chat.dto.ChatRoomListResultDTO;
import java.util.List;

public interface ChatService {
    List<ChatRoomListResultDTO> getChatRoomList();
}
