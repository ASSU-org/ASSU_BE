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
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CheckStudentReviewResponseDTO { //내가 작성한 리뷰
        private Long reviewId;
        private Long storeId;
        private String content;
        private Integer rate;
        private LocalDateTime createdAt;
        //private List<ReviewPhoto> reviewImage;
    }


}
