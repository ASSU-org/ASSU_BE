package com.assu.server.domain.chat.service;

import com.assu.server.domain.chat.converter.BlockConverter;
import com.assu.server.domain.chat.converter.ChatConverter;
import com.assu.server.domain.chat.dto.BlockResponseDTO;
import com.assu.server.domain.chat.entity.Block;
import com.assu.server.domain.chat.repository.BlockRepository;
import com.assu.server.domain.common.enums.UserRole;
import com.assu.server.domain.member.entity.Member;
import com.assu.server.domain.member.repository.MemberRepository;
import com.assu.server.global.apiPayload.code.status.ErrorStatus;
import com.assu.server.global.exception.GeneralException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlockServiceImpl implements BlockService {
    private final BlockRepository blockRepository;
    private final MemberRepository memberRepository;

    @Transactional
    @Override
    public BlockResponseDTO.BlockMemberDTO blockMember(Long blockerId, Long blockedId) {
        if (blockerId.equals(blockedId)) {
            throw new GeneralException(ErrorStatus._BAD_REQUEST);
        }

        Member blocker = memberRepository.findById(blockerId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._BAD_REQUEST));
        Member blocked = memberRepository.findById(blockedId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._BAD_REQUEST));


        // 이미 차단했는지 확인
        if (blockRepository.existsByBlockerAndBlocked(blocker, blocked)) {
            // 이미 차단한 경우, 아무것도 하지 않거나 예외 처리
            return null;
        }

        UserRole blockedRole = blocked.getRole();
        String blockedName;
        if (blockedRole == UserRole.ADMIN) {
            blockedName = blocked.getAdminProfile().getName();
        } else if (blockedRole == UserRole.PARTNER) {
            blockedName = blocked.getPartnerProfile().getName();
        } else {
            throw new GeneralException(ErrorStatus._BAD_REQUEST);
        }

        Block block = Block.builder()
                .blocker(blocker)
                .blocked(blocked)
                .build();

        blockRepository.save(block);

        return BlockConverter.toBlockDTO(blockedId,  blockedName);
    }

    @Override
    public BlockResponseDTO.CheckBlockMemberDTO checkBlock(Long blockerId, Long blockedId) {

        Member blocker = memberRepository.findById(blockerId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._BAD_REQUEST));
        Member blocked = memberRepository.findById(blockedId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._BAD_REQUEST));

        UserRole blockedRole = blocked.getRole();
        String blockedName;
        if (blockedRole == UserRole.ADMIN) {
            blockedName = blocked.getAdminProfile().getName();
        } else if (blockedRole == UserRole.PARTNER) {
            blockedName = blocked.getPartnerProfile().getName();
        } else {
            throw new GeneralException(ErrorStatus._BAD_REQUEST);
        }

        if (blockRepository.existsBlockRelationBetween(blocker, blocked)) {
            return BlockConverter.toCheckBlockDTO(blockedId, blockedName, true);
        }
        else  {
            return BlockConverter.toCheckBlockDTO(blockedId, blockedName, false);
        }
    }

    @Transactional
    @Override
    public BlockResponseDTO.BlockMemberDTO unblockMember(Long blockerId, Long blockedId) {
        Member blocker = memberRepository.findById(blockerId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NO_SUCH_MEMBER));
        Member blocked = memberRepository.findById(blockedId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NO_SUCH_MEMBER));

        UserRole blockedRole = blocked.getRole();
        String blockedName;
        if (blockedRole == UserRole.ADMIN) {
            blockedName = blocked.getAdminProfile().getName();
        } else if (blockedRole == UserRole.PARTNER) {
            blockedName = blocked.getPartnerProfile().getName();
        } else {
            throw new GeneralException(ErrorStatus._BAD_REQUEST);
        }

        // Transactional 환경에서는 Dirty-checking으로 delete 쿼리가 나갑니다.
        blockRepository.deleteByBlockerAndBlocked(blocker, blocked);
        return BlockConverter.toBlockDTO(blockedId, blockedName);
    }

    @Transactional
    @Override
    public List<BlockResponseDTO.BlockMemberDTO> getMyBlockList(Long blockerId) {
        Member blocker = memberRepository.findById(blockerId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._BAD_REQUEST));

        List<Block> blockList = blockRepository.findByBlocker(blocker);

        return BlockConverter.toBlockedMemberListDTO(blockList);
    }
}
