package com.assu.server.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class StudentResponseDTO {
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

}
