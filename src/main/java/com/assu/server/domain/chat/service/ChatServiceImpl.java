package com.assu.server.domain.chat.service;

import com.assu.server.domain.admin.entity.Admin;
import com.assu.server.domain.admin.repository.AdminRepository;
import com.assu.server.domain.chat.converter.ChatConverter;
import com.assu.server.domain.chat.dto.ChatMessageDTO;
import com.assu.server.domain.chat.dto.ChatRequestDTO;
import com.assu.server.domain.chat.dto.ChatResponseDTO;
import com.assu.server.domain.chat.dto.ChatRoomListResultDTO;
import com.assu.server.domain.chat.entity.ChattingRoom;
import com.assu.server.domain.chat.entity.Message;
import com.assu.server.domain.chat.repository.ChatRepository;
import com.assu.server.domain.chat.repository.MessageRepository;
import com.assu.server.domain.member.entity.Member;
import com.assu.server.domain.common.enums.ActivationStatus;
import com.assu.server.domain.member.repository.MemberRepository;
import com.assu.server.domain.partner.entity.Partner;
import com.assu.server.domain.partner.repository.PartnerRepository;
import com.assu.server.global.apiPayload.code.status.ErrorStatus;
import com.assu.server.global.exception.DatabaseException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final ChatRepository chatRepository;
    private final MemberRepository memberRepository;
    private final PartnerRepository partnerRepository;
    private final AdminRepository adminRepository;
    private final MessageRepository messageRepository;


    @Override
    public List<ChatRoomListResultDTO> getChatRoomList() {
//        Long memberId = SecurityUtil.getCurrentUserId;
        Long memberId = 1L;

        List<ChatRoomListResultDTO> chatRoomList = chatRepository.findChattingRoomsByMemberId(memberId);
        return ChatConverter.toChatRoomListResultDTO(chatRoomList);
    }

    @Override
    public ChatResponseDTO.CreateChatRoomResponseDTO createChatRoom(ChatRequestDTO.CreateChatRoomRequestDTO request) {
//        Long memberId = SecurityUtil.getCurrentUserId;
//        Long opponentId = request.getOpponentId();

        Long adminId = request.getAdminId();
        Long partnerId = request.getPartnerId();

        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_ADMIN));
        Partner partner = partnerRepository.findById(partnerId)
                .orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_PARTNER));

        ChattingRoom room = ChatConverter.toCreateChattingRoom(admin, partner);

        room.updateStatus(ActivationStatus.ACTIVE);

        room.updateMemberCount(2);

        room.updateName(
                partner.getName(),
                admin.getName()
        );
        ChattingRoom savedRoom = chatRepository.save(room);



        return ChatConverter.toCreateChatRoomIdDTO(savedRoom);
    }

    @Override
    public ChatResponseDTO.SendMessageResponseDTO handleMessage(ChatRequestDTO.ChatMessageRequestDTO request) {
        // 유효성 검사
        ChattingRoom room = chatRepository.findById(request.roomId())
                .orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_ROOM));
        Member sender = memberRepository.findById(request.senderId())
                .orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_MEMBER));
        Member receiver = memberRepository.findById(request.receiverId())
                .orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_MEMBER));

        Message message = ChatConverter.toMessageEntity(request, room, sender, receiver);
        messageRepository.save(message);

        return ChatConverter.toSendMessageDTO(message);
    }

    @Transactional
    @Override
    public ChatResponseDTO.ReadMessageResponseDTO readMessage(Long roomId) {
//        Long memberId = SecurityUtil.getCurrentUserId();
        Long memberId = 2L;

        List<Message> unreadMessages = messageRepository.findUnreadMessagesByRoomAndReceiver(roomId, memberId);

        unreadMessages.forEach(Message::markAsRead);

        return new ChatResponseDTO.ReadMessageResponseDTO(roomId, unreadMessages.size());
    }

    @Override
    public ChatResponseDTO.ChatHistoryResponseDTO readHistory(Long roomId) {
//        Long memberId = SecurityUtil.getCurrentUserId();
        Long memberId = 1L;

        ChattingRoom room = chatRepository.findById(roomId)
                .orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_ROOM));

        List<ChatMessageDTO> allMessages = messageRepository.findAllMessagesByRoomAndMemberId(roomId, memberId);

        return ChatConverter.toChatHistoryDTO(roomId, allMessages);
    }

    @Override
    public ChatResponseDTO.LeaveChattingRoomResponseDTO leaveChattingRoom(Long roomId) {
//        Long memberId = SecurityUtil.getCurrentUserId();

        Long memberId = 2L;

        // 멤버 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_MEMBER));

        // 채팅방 조회
        ChattingRoom chattingRoom = chatRepository.findById(roomId)
                .orElseThrow(() -> new DatabaseException(ErrorStatus.NO_MEMBER_IN_THE_ROOM));

        boolean isAdmin = chattingRoom.getAdmin() != null &&
                chattingRoom.getAdmin().getMember().getId().equals(member.getId());
        boolean isPartner = chattingRoom.getPartner() != null &&
                chattingRoom.getPartner().getMember().getId().equals(member.getId());

        int memberCount = chattingRoom.getMemberCount();
        boolean isRoomDeleted = false;
        boolean isLeftSuccessfully = false;

        if(memberCount == 2) {
            if (isAdmin) {
                chattingRoom.setAdmin(null);
            } else if (isPartner) {
                chattingRoom.setPartner(null);
            } else {
                throw new DatabaseException(ErrorStatus.NO_SUCH_MEMBER);
            }
            chattingRoom.updateMemberCount(1);
            isLeftSuccessfully = true;
            chatRepository.save(chattingRoom);
        } else if(memberCount == 1) {
            isRoomDeleted = true;
            isLeftSuccessfully = true;
            chatRepository.delete(chattingRoom);

        } else if(memberCount == 0) {
            throw new DatabaseException(ErrorStatus.NO_MEMBER);
        }
        return new  ChatResponseDTO.LeaveChattingRoomResponseDTO(roomId, isLeftSuccessfully,isRoomDeleted);
    }
}
