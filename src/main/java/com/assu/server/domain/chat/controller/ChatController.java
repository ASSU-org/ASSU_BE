package com.assu.server.domain.chat.controller;

import com.assu.server.domain.chat.dto.ChatRequestDTO;
import com.assu.server.domain.chat.dto.ChatResponseDTO;
import com.assu.server.domain.chat.service.ChatService;
import com.assu.server.global.apiPayload.code.status.SuccessStatus;
import com.assu.server.global.util.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.assu.server.global.apiPayload.BaseResponse;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {
    private final ChatService chatService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @Operation(
            summary = "채팅방 목록을 조회하는 API",
            description = "Request Header에 User id를 입력해 주세요."
    )
    @GetMapping("/rooms")
    public BaseResponse<List<com.assu.server.domain.chat.dto.ChatRoomListResultDTO>> getChatRoomList(
            @AuthenticationPrincipal PrincipalDetails pd
            ) {
        Long memberId = pd.getMember().getId();
        return BaseResponse.onSuccess(SuccessStatus._OK, chatService.getChatRoomList(memberId));
    }

    @Operation(
            summary = "채팅방을 생성하는 API",
            description = "상대방의 id를 request body에 입력해 주세요"
    )
    @PostMapping("/create/rooms")
    public BaseResponse<ChatResponseDTO.CreateChatRoomResponseDTO> createChatRoom(
            @AuthenticationPrincipal PrincipalDetails pd,
            @RequestBody ChatRequestDTO.CreateChatRoomRequestDTO request) {
        Long memberId = pd.getMember().getId();
        return BaseResponse.onSuccess(SuccessStatus._OK, chatService.createChatRoom(request, memberId));
    }

    @Operation(
            summary = "채팅 API.",
            description = "roomId, senderId, message를 입력해 주세요"
    )
    @MessageMapping("/send")
    public void handleMessage(@Payload ChatRequestDTO.ChatMessageRequestDTO request) {
        ChatResponseDTO.SendMessageResponseDTO response = chatService.handleMessage(request);

        simpMessagingTemplate.convertAndSend("/sub/chat/" + request.roomId(), response);
    }

    @Operation(
            summary = "메시지 읽음 처리 API",
            description = "roomId를 입력해 주세요."
    )
    @PatchMapping("rooms/{roomId}/read")
    public BaseResponse<ChatResponseDTO.ReadMessageResponseDTO> readMessage(
            @AuthenticationPrincipal PrincipalDetails pd,
            @PathVariable Long roomId
    ) {
        Long memberId = pd.getMember().getId();
        ChatResponseDTO.ReadMessageResponseDTO response = chatService.readMessage(roomId, memberId);
        return BaseResponse.onSuccess(SuccessStatus._OK, response);
    }

    @Operation(
            summary = "채팅방 상세 조회 API",
            description = "roomId를 입력해 주세요."
    )
    @GetMapping("rooms/{roomId}/messages")
    public BaseResponse<ChatResponseDTO.ChatHistoryResponseDTO> getChatHistory(
            @AuthenticationPrincipal PrincipalDetails pd,
            @PathVariable Long roomId
    ) {
        Long memberId = pd.getMember().getId();
        ChatResponseDTO.ChatHistoryResponseDTO response = chatService.readHistory(roomId, memberId);
        return BaseResponse.onSuccess(SuccessStatus._OK, response);
    }

    @Operation(
            summary = "채팅방을 나가는 API" +
                    "참여자가 2명이면 채팅방이 살아있지만, 이미 한 명이 나갔다면 채팅방이 삭제됩니다.",
            description = "roomId를 입력해 주세요."
    )
    @DeleteMapping("rooms/{roomId}/leave")
    public BaseResponse<ChatResponseDTO.LeaveChattingRoomResponseDTO> leaveChattingRoom(
            @AuthenticationPrincipal PrincipalDetails pd,
            @PathVariable Long roomId
    ) {
        Long memberId = pd.getMember().getId();
        return BaseResponse.onSuccess(SuccessStatus._OK, chatService.leaveChattingRoom(roomId, memberId));
    }
}
