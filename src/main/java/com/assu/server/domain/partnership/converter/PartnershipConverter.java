package com.assu.server.domain.partnership.converter;

import java.time.LocalDate;

import com.assu.server.domain.common.entity.Member;
import com.assu.server.domain.partnership.dto.PartnershipRequestDTO;
import com.assu.server.domain.user.entity.PartnershipUsage;

public class PartnershipConverter {

	public static PartnershipUsage toPartnershipUsage(PartnershipRequestDTO.finalRequest dto, Member member){
		return PartnershipUsage.builder()
			.date(LocalDate.now())
			.place(dto.getPlaceName())
			.student(member.getStudentProfile())
			.isReviewed(false)
			.partnershipContent(dto.getPartnershipContent())
			.build();
	}
}
