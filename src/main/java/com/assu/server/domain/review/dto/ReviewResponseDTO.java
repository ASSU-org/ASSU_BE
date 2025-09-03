package com.assu.server.domain.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

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
        private List<String> reviewImageUrls;
    }
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CheckStudentReviewResponseDTO { //내가 작성한 리뷰
        private Long reviewId;
        private Long storeId;
        private String storeName;
        private String content;
        private Integer rate;
        private LocalDateTime createdAt;
        private List<String> reviewImageUrls;
    }
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CheckPartnerReviewResponseDTO {//partner의 리뷰 확인
        private Long reviewId;
        private Long storeId; //현재 파트너의 가게 아이디
        private Long reviewerId;
        private String content;
        private Integer rate;
        private LocalDateTime createdAt;
        private List<String> reviewImageUrls;
    }


    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DeleteReviewResponseDTO {
        private Long reviewId;
    }

}
