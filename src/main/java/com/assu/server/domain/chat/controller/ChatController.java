package com.assu.server.domain.chat.controller;

import com.assu.server.domain.chat.dto.ChatRequestDTO;
import com.assu.server.domain.chat.dto.ChatResponseDTO;
import com.assu.server.domain.chat.service.ChatService;
import com.assu.server.global.apiPayload.code.status.SuccessStatus;
import com.assu.server.global.util.PresenceTracker;
import com.assu.server.global.util.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.assu.server.global.apiPayload.BaseResponse;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {
    private final ChatService chatService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final PresenceTracker presenceTracker;

    @Operation(
            summary = "채팅방을 생성하는 API",
            description = "# [v1.0 (2025-08-05)](https://clumsy-seeder-416.notion.site/2241197c19ed80c38871ec77deced713) 채팅방을 생성합니다.\n"+
                    "- storeId: Request Body, Long\n" +
                    "- partnerId: Request Body, Long\n"
    )
    @PostMapping("/rooms")
    public BaseResponse<ChatResponseDTO.CreateChatRoomResponseDTO> createChatRoom(
            @AuthenticationPrincipal PrincipalDetails pd,
            @RequestBody ChatRequestDTO.CreateChatRoomRequestDTO request) {
        Long memberId = pd.getMember().getId();
        return BaseResponse.onSuccess(SuccessStatus._OK, chatService.createChatRoom(request, memberId));
    }

    @Operation(
            summary = "채팅방 목록을 조회하는 API",
            description = "# [v1.0 (2025-08-05)](https://clumsy-seeder-416.notion.site/API-1d71197c19ed819f8f70fb437e9ce62b?p=2241197c19ed816993c3c5ae17d6f099&pm=s) 채팅방 목록을 조회합니다.\n"
    )
    @GetMapping("/rooms")
    public BaseResponse<List<com.assu.server.domain.chat.dto.ChatRoomListResultDTO>> getChatRoomList(
            @AuthenticationPrincipal PrincipalDetails pd
    ) {
        Long memberId = pd.getMember().getId();
        return BaseResponse.onSuccess(SuccessStatus._OK, chatService.getChatRoomList(memberId));
    }

    @Operation(
            summary = "채팅 API",
            description = "# [v1.0 (2025-08-05)](https://clumsy-seeder-416.notion.site/2241197c19ed800eab45c35073761c97?v=2241197c19ed8134b64f000cc26c5d31&p=2371197c19ed80968342e2bc8fe88cee&pm=s) 메시지를 전송합니다.\n"+
                    "- roomId: Request Body, Long\n" +
                    "- senderId: Request Body, Long\n"+
                    "- receiverId: Request Body, Long\n" +
                    "- message: Request Body, String\n"
    )
    @MessageMapping("/send")
    public void handleMessage(@Payload ChatRequestDTO.ChatMessageRequestDTO request) {
        // 먼저 접속 여부 확인 후 unreadCount 계산
        boolean receiverInRoom = presenceTracker.isInRoom(request.getReceiverId(), request.getRoomId());
        int unreadForSender = receiverInRoom ? 0 : 1;
        request.setUnreadCountForSender(unreadForSender);
//        log.info("[WS] handleMessage IN: {}", request);   // ★ 호출 여부 확인
        ChatResponseDTO.SendMessageResponseDTO saved = chatService.handleMessage(request);

        log.info(">>>> [CHECK 1] 수신자 ID {}의 접속 상태: {}, 계산된 unreadCount: {}",
                request.getReceiverId(), receiverInRoom, unreadForSender);

        log.info(">>>> [CHECK 2] 브로드캐스팅 직전 메시지: {}", saved);
        // 잘 전송됐는지 확인용
//        String destination = "/sub/chat/" + request.roomId();
//        log.info("[WS] convertAndSend → destination={}, payload={}", destination, response);
        simpMessagingTemplate.convertAndSend("/sub/chat/" + request.getRoomId(), saved);
    }

    @Operation(
            summary = "메시지 읽음 처리 API",
            description = "# [v1.0 (2025-08-05)](https://clumsy-seeder-416.notion.site/2241197c19ed800eab45c35073761c97?v=2241197c19ed8134b64f000cc26c5d31&p=2241197c19ed81ffa771cb18ab157b54&pm=s) 메시지를 읽음처리합니다.\n"+
                    "- roomId: Path Variable, Long\n"
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
            description = "# [v1.0 (2025-08-05)](https://clumsy-seeder-416.notion.site/2241197c19ed800eab45c35073761c97?v=2241197c19ed8134b64f000cc26c5d31&p=2241197c19ed81399395fd66f73730af&pm=s) 채팅방을 클릭했을 때 메시지를 조회합니다.\n"+
                    "- roomId: Path Variable, Long\n"
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
            description = "# [v1.0 (2025-08-05)](https://clumsy-seeder-416.notion.site/2241197c19ed800eab45c35073761c97?v=2241197c19ed8134b64f000cc26c5d31&p=2371197c19ed8079a6e1c2331cb4f534&pm=s) 채팅방을 나갑니다.\n"+
                    "- roomId: Path Variable, Long\n"
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
