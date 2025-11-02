package com.assu.server.domain.user.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.assu.server.domain.partnership.entity.enums.CriterionType;
import com.assu.server.domain.partnership.entity.enums.OptionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
		private String adminName;
		private Long partnershipUsageId;
		private String storeName;
		private Long partnerId;
		private Long storeId;
		private String usedAt;
		private String benefitDescription;
		private boolean isReviewed;
	}
   /* @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CheckPartnershipUsageResponseDTO {
        private Long id;
        private String place;
        private LocalDate date;
        private String partnershipContent;
        private Boolean isReviewed; //리뷰 작성하기 버튼 활성화 ?
        private Integer discount; //가격? 비율
        private LocalDateTime createdAt;
    }
    */

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CheckStampResponseDTO {
        private Long userId;
        private int stamp;
        private String message;
    }

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class UsablePartnershipDTO {
		private Long partnershipId;
		private String adminName;
		private String partnerName;
		private CriterionType criterionType;
		private OptionType optionType;
		private Integer people;
		private Long cost;
		private String note;
		private Long paperId;
		private String category;
		private Long discountRate;
	}

}
