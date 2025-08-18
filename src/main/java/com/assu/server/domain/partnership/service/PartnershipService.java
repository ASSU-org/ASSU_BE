package com.assu.server.domain.partnership.service;

import com.assu.server.domain.member.entity.Member;
import com.assu.server.domain.partnership.dto.PartnershipRequestDTO;

public interface PartnershipService {
	void recordPartnershipUsage(PartnershipRequestDTO.finalRequest dto, Member member);
}
