package com.assu.server.domain.partnership.dto;

import java.util.List;

import lombok.Getter;

public class PartnershipRequestDTO {

	@Getter
	public static class finalRequest{
		String placeName;
		String partnershipContent;
		Long contentId;
		Long discount;
		List<Long> userIds;
	}
}
