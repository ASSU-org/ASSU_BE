package com.assu.server.domain.chat.controller;

import com.assu.server.domain.chat.dto.ChatRequestDTO;
import com.assu.server.domain.chat.dto.ChatResponseDTO;
import com.assu.server.domain.chat.service.ChatService;
import com.assu.server.global.apiPayload.code.status.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.assu.server.global.apiPayload.BaseResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {
    private final ChatService chatService;

    @Operation(
            summary = "채팅방 목록을 조회하는 API 입니다.",
            description = "Request Header에 User id를 입력해 주세요."
    )
    @GetMapping("/rooms")
    public BaseResponse<List<com.assu.server.domain.chat.dto.ChatRoomListResultDTO>> getChatRoomList() {
        return BaseResponse.onSuccess(SuccessStatus._OK, chatService.getChatRoomList());
    }

    @Operation(
            summary = "채팅방을 생성하는 API 입니다.",
            description = "상대방의 id를 request body에 입력해 주세요"
    )
    @PostMapping("/create/rooms")
    public BaseResponse<ChatResponseDTO.CreateChatRoomResponseDTO> createChatRoom(@RequestBody ChatRequestDTO.CreateChatRoomRequestDTO request) {
        return BaseResponse.onSuccess(SuccessStatus._OK, chatService.createChatRoom(request));
    }
}
