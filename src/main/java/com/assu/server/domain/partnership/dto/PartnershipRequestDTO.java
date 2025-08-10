package com.assu.server.domain.partnership.dto;

import lombok.Getter;

public class PartnershipRequestDTO {

	@Getter
	public static class finalRequest{
		String placeName;
		String partnershipContent;
		Long contentId;
		Long discount;
	}
}
