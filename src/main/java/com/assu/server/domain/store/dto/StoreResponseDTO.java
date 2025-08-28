package com.assu.server.domain.store.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;



public class StoreResponseDTO {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class WeeklyRankResponseDTO {
            private Long rank;           // 그 주 순위(1부터)
            private Long usageCount;     // 그 주 사용 건수
    }
	@AllArgsConstructor
	@RequiredArgsConstructor
	@Builder
	@Getter
	public static class todayBest{
		List<String> bestStores;
	}
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ListWeeklyRankResponseDTO {
        private Long storeId;
        private String storeName;
        private List<WeeklyRankResponseDTO> items; // 과거→현재 (6개)
    }

}
