package com.assu.server.domain.user.dto;

import java.time.LocalDate;
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
		private long serviceCount;
		private List<UsageDetailDTO> details;
	}

	@Getter
	@AllArgsConstructor
	@Builder
	public static class UsageDetailDTO {
		private String storeName;
		private LocalDate usedAt;
		private String benefitDescription;
		private boolean isReviewed;
	}
}
