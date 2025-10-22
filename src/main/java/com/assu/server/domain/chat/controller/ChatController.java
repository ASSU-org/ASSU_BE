package com.assu.server.domain.chat.controller;

import com.assu.server.domain.chat.dto.*;
import com.assu.server.domain.chat.repository.MessageRepository;
import com.assu.server.domain.chat.service.BlockService;
import com.assu.server.domain.chat.service.ChatService;
import com.assu.server.domain.common.enums.UserRole;
import com.assu.server.domain.member.entity.Member;
import com.assu.server.domain.member.repository.MemberRepository;
import com.assu.server.domain.notification.service.NotificationCommandService;
import com.assu.server.global.apiPayload.code.status.SuccessStatus;
import com.assu.server.global.util.PresenceTracker;
import com.assu.server.global.util.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.transaction.Transactional;
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
    private final MessageRepository messageRepository;
    private final MemberRepository memberRepository;
    private final BlockService blockService;
    private final NotificationCommandService notificationCommandService;

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
    @Transactional
    @MessageMapping("/send")
    public void handleMessage(@Payload ChatRequestDTO.ChatMessageRequestDTO request) {
        // 먼저 접속 여부 확인 후 unreadCount 계산
        boolean receiverInRoom = presenceTracker.isInRoom(request.getReceiverId(), request.getRoomId());
        int unreadForSender = receiverInRoom ? 0 : 1;
        request.setUnreadCountForSender(unreadForSender);

        ChatResponseDTO.SendMessageResponseDTO saved = chatService.handleMessage(request);
        simpMessagingTemplate.convertAndSend("/sub/chat/" + request.getRoomId(), saved);

        if (!receiverInRoom) {
            Long totalUnreadCount = messageRepository.countUnreadMessagesByRoomAndReceiver(
                    request.getRoomId(),
                    request.getReceiverId()
            );

            ChatRoomUpdateDTO updateDTO = ChatRoomUpdateDTO.builder()
                    .roomId(request.getRoomId())
                    .lastMessage(saved.message())
                    .lastMessageTime(saved.sentAt())
                    .unreadCount(totalUnreadCount)
                    .build();

            simpMessagingTemplate.convertAndSendToUser(
                    request.getReceiverId().toString(),
                    "/queue/updates",
                    updateDTO
            );
            Member sender = memberRepository.findById(request.getSenderId()).orElse(null);
            String senderName;
            if (sender.getRole()== UserRole.ADMIN) {
                senderName = sender.getAdminProfile().getName();
            } else {
                senderName = sender.getPartnerProfile().getName();
            }

            log.info(">>>>>>>>메시지 전송은 될걸");
            notificationCommandService.sendChat(request.getReceiverId(), request.getRoomId(), senderName, request.getMessage());
            log.info(">>>>>>>>알림이 가나");
        }
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

    @Operation(
            summary = "상대방을 차단하는 API" +
                    "상대방을 차단합니다. 메시지를 주고받을 수 없습니다.",
            description = "# [v1.0 (2025-09-25)]() 상대방을 차단합니다.\n"+
                    "- memberId: Request Body, Long\n"
    )
    @PostMapping("/block")
    public BaseResponse<BlockResponseDTO.BlockMemberDTO> block(
            @AuthenticationPrincipal PrincipalDetails pd,
            @RequestBody BlockRequestDTO.BlockMemberRequestDTO request
            ) {
        Long memberId = pd.getMember().getId();
        return BaseResponse.onSuccess(SuccessStatus._OK, blockService.blockMember(memberId, request.getOpponentId()));
    }

    @Operation(
            summary = "상대방을 차단했는지 확인하는 API" +
                    "상대방을 차단했는지 여부를 알려줍니다.",
            description = "# [v1.0 (2025-09-25)]() 상대방을 차단했는지 검사합니다.\n"+
                    "- memberId: Request Body, Long\n"
    )
    @GetMapping("/check/block/{opponentId}")
    public BaseResponse<BlockResponseDTO.CheckBlockMemberDTO> checkBlock(
            @AuthenticationPrincipal PrincipalDetails pd,
            @PathVariable Long opponentId
    ) {
        Long memberId = pd.getMember().getId();
        return BaseResponse.onSuccess(SuccessStatus._OK, blockService.checkBlock(memberId, opponentId));
    }

    @Operation(
            summary = "상대방을 차단 해제하는 API" +
                    "상대방을 차단해제합니다. 앞으로 다시 메시지를 주고받을 수 있습니다.",
            description = "# [v1.0 (2025-09-25)]() 상대방을 차단 해제합니다.\n"+
                    "- memberId: Request Body, Long\n"
    )
    @DeleteMapping("/unblock")
    public BaseResponse<BlockResponseDTO.BlockMemberDTO> unblock(
            @AuthenticationPrincipal PrincipalDetails pd,
            @RequestParam Long opponentId
    ) {
        Long memberId = pd.getMember().getId();
        return BaseResponse.onSuccess(SuccessStatus._OK, blockService.unblockMember(memberId, opponentId));
    }

    @Operation(
            summary = "차단한 대상을 조회합니다." +
                    "본인이 차단한 대상을 모두 조회합니다.",
            description = "# [v1.0 (2025-09-25)]() 차단한 대상을 조회합니다..\n"+
                    "- memberId: Request Body, Long\n"
    )
    @GetMapping("/blockList")
    public BaseResponse<List<BlockResponseDTO.BlockMemberDTO>> getBlockList(
            @AuthenticationPrincipal PrincipalDetails pd
    ) {
        Long memberId = pd.getMember().getId();
        return BaseResponse.onSuccess(SuccessStatus._OK, blockService.getMyBlockList(memberId));
    }

}
