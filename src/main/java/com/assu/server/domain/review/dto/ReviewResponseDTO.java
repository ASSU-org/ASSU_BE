package com.assu.server.domain.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class ReviewResponseDTO {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class WriteReviewResponseDTO {
        private Long reviewId; //entity 보고 형 맞추기
        private String content;
        private Integer rate;
        private LocalDateTime createdAt;
        private Long memberId;
    }

}
