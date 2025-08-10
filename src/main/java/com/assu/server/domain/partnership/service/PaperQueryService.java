package com.assu.server.domain.partnership.service;

import com.assu.server.domain.common.entity.Member;
import com.assu.server.domain.partnership.dto.PaperResponseDTO;

public interface PaperQueryService {
	PaperResponseDTO.partnershipContent getStorePaperContent(Long storeId, Member member);
}
