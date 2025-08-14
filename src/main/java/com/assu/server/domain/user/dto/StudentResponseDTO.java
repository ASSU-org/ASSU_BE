package com.assu.server.domain.user.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class StudentResponseDTO {

	@Getter
	@Builder
	@AllArgsConstructor
	@RequiredArgsConstructor
	public static class myPartnership {
		private long serviceCount;        // SERVICE 개수
		private int totalDiscount;        // DISCOUNT 총액
		private List<UsageDetailDTO> usageDetails;
	}

	@Getter
	@AllArgsConstructor
	public static class UsageDetailDTO {
		private String storeName;
		private LocalDateTime usedAt;
		private String benefitDescription;
		private boolean isReviewed;
	}
}
