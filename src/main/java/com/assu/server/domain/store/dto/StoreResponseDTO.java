package com.assu.server.domain.store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class StoreResponseDTO {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class WeeklyRankResponseDTO {
            private Long rank;           // 그 주 순위(1부터)
            private Long usageCount;     // 그 주 사용 건수
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
