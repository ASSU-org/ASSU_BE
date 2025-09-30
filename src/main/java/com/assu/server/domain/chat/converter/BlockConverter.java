package com.assu.server.domain.chat.converter;

import com.assu.server.domain.chat.dto.BlockResponseDTO;
import com.assu.server.domain.chat.entity.Block;
import com.assu.server.domain.common.enums.UserRole;
import com.assu.server.domain.member.entity.Member;

import java.util.List;
import java.util.stream.Collectors;

public class BlockConverter {
    public static BlockResponseDTO.BlockMemberDTO toBlockDTO(Long blockedId, String blockedName) {
        return BlockResponseDTO.BlockMemberDTO.builder()
                .memberId(blockedId)
                .name(blockedName)
                .build();
    }

    public static BlockResponseDTO.CheckBlockMemberDTO toCheckBlockDTO(Long blockedId, String blockedName, boolean blocked) {
        return BlockResponseDTO.CheckBlockMemberDTO.builder()
                .memberId(blockedId)
                .name(blockedName)
                .blocked(blocked)
                .build();
    }

    public static BlockResponseDTO.BlockMemberDTO toBlockedMemberDTO(Block block) {
        // Block 엔티티에서 차단된 사용자(Member) 정보를 꺼냅니다.
        Member blockedMember = block.getBlocked();
        UserRole blockedRole = blockedMember.getRole();
        String blockedName;
        if (blockedRole == UserRole.ADMIN) {
            blockedName = blockedMember.getAdminProfile().getName();
        } else {
            blockedName = blockedMember.getPartnerProfile().getName();
        }

        return BlockResponseDTO.BlockMemberDTO.builder()
                .memberId(blockedMember.getId())
                .name(blockedName) // 또는 getNickname() 등 실제 필드명 사용
                .blockDate(block.getCreatedAt())
                .build();
    }

    public static List<BlockResponseDTO.BlockMemberDTO> toBlockedMemberListDTO(List<Block> blockList) {
        return blockList.stream()
                .map(BlockConverter::toBlockedMemberDTO) // 각 Block 객체에 대해 위 헬퍼 메소드를 호출
                .collect(Collectors.toList());
    }

}
