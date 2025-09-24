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
import com.assu.server.domain.store.entity.Store;
import com.assu.server.domain.store.repository.StoreRepository;
import com.assu.server.global.apiPayload.code.status.ErrorStatus;
import com.assu.server.global.exception.DatabaseException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final ChatRepository chatRepository;
    private final MemberRepository memberRepository;
    private final PartnerRepository partnerRepository;
    private final AdminRepository adminRepository;
    private final MessageRepository messageRepository;
    private final StoreRepository storeRepository;


    @Override
    public List<ChatRoomListResultDTO> getChatRoomList(Long memberId) {

        List<ChatRoomListResultDTO> chatRoomList = chatRepository.findChattingRoomsByMemberId(memberId);
        return ChatConverter.toChatRoomListResultDTO(chatRoomList);
    }

    @Override
    public ChatResponseDTO.CreateChatRoomResponseDTO createChatRoom(ChatRequestDTO.CreateChatRoomRequestDTO request, Long memberId) {

        Long adminId = request.getAdminId();
        Long partnerId = request.getPartnerId();

        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_ADMIN));
        Partner partner = partnerRepository.findById(partnerId)
                .orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_PARTNER));
        Store store = storeRepository.findByPartnerId(partnerId)
                .orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_STORE));


        if (!store.getPartner().getMember().getId().equals(partner.getMember().getId())) {
            throw new DatabaseException(ErrorStatus.NO_SUCH_STORE_WITH_THAT_PARTNER);
        }

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
    @Transactional
    public ChatResponseDTO.SendMessageResponseDTO handleMessage(ChatRequestDTO.ChatMessageRequestDTO request) {
        // 유효성 검사
        ChattingRoom room = chatRepository.findById(request.getRoomId())
                .orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_ROOM));
        Member sender = memberRepository.findById(request.getSenderId())
                .orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_MEMBER));
        Member receiver = memberRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_MEMBER));

        Message message = ChatConverter.toMessageEntity(request, room, sender, receiver);
        Message saved = messageRepository.saveAndFlush(message);
        log.info("saved message id={}, roomId={}, senderId={}, receiverId={}",
                saved.getId(), room.getId(), sender.getId(), receiver.getId());

        boolean exists = messageRepository.existsById(saved.getId());
        log.info("Saved? {}", exists); // true 아니면 트랜잭션/DB 문제
        return ChatConverter.toSendMessageDTO(saved);
    }

    @Transactional
    @Override
    public ChatResponseDTO.ReadMessageResponseDTO readMessage(Long roomId, Long memberId) {

        List<Message> unreadMessages = messageRepository.findUnreadMessagesByRoomAndReceiver(roomId, memberId);
        List<Long> readMessagesIdList = new ArrayList<>();

        for(Message unreadMessage : unreadMessages) {
            readMessagesIdList.add(unreadMessage.getId());
        }
        unreadMessages.forEach(Message::markAsRead);


        return new ChatResponseDTO.ReadMessageResponseDTO(roomId, memberId,readMessagesIdList, unreadMessages.size(), true);
    }

    @Override
    public ChatResponseDTO.ChatHistoryResponseDTO readHistory(Long roomId, Long memberId) {

        ChattingRoom room = chatRepository.findById(roomId)
                .orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_ROOM));

        List<ChatMessageDTO> allMessages = messageRepository.findAllMessagesByRoomAndMemberId(room.getId(), memberId);

        return ChatConverter.toChatHistoryDTO(room.getId(), allMessages);
    }

    @Override
    public ChatResponseDTO.LeaveChattingRoomResponseDTO leaveChattingRoom(Long roomId, Long memberId) {
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
            if (isAdmin) {
                chattingRoom.setAdmin(null);
            } else if (isPartner) {
                chattingRoom.setPartner(null);
            }
            chattingRoom.updateMemberCount(0);
            isLeftSuccessfully = true;

            // ✅ 방에 아무도 안 남았을 때만 삭제
            if (chattingRoom.getAdmin() == null && chattingRoom.getPartner() == null) {
                isRoomDeleted = true;
                chatRepository.delete(chattingRoom);
            } else {
                chatRepository.save(chattingRoom);
            }

        } else if(memberCount == 0) {
            throw new DatabaseException(ErrorStatus.NO_MEMBER);
        }
        return new  ChatResponseDTO.LeaveChattingRoomResponseDTO(roomId, isLeftSuccessfully,isRoomDeleted);
    }
}
