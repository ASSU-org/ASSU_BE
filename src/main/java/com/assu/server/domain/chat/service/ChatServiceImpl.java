package com.assu.server.domain.chat.service;

import com.assu.server.domain.chat.converter.ChatConverter;
import com.assu.server.domain.chat.dto.ChatRoomListResultDTO;
import com.assu.server.domain.chat.repository.ChatRepository;
import com.assu.server.domain.common.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final ChatRepository chatRepository;
    private final MemberRepository memberRepository;

    @Override
    public List<ChatRoomListResultDTO> getChatRoomList() {
//        Long memberId = SecurityUtil.getCurrentUserId;
        Long memberId = 1L;

        List<ChatRoomListResultDTO> chatRoomList = chatRepository.findChattingRoomByMember(memberId);
        return ChatConverter.toChatRoomListResultDTO(chatRoomList);
    }
}
