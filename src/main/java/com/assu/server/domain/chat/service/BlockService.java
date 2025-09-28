package com.assu.server.domain.chat.service;

import com.assu.server.domain.chat.dto.BlockResponseDTO;

import java.util.List;

public interface BlockService {
    BlockResponseDTO.BlockMemberDTO blockMember(Long blockerId, Long blockedId);
    BlockResponseDTO.CheckBlockMemberDTO checkBlock(Long blockerId, Long blockedId);
    BlockResponseDTO.BlockMemberDTO unblockMember(Long blockerId, Long blockedId);
    List<BlockResponseDTO.BlockMemberDTO> getMyBlockList(Long blockerId);
}
