package com.assu.server.domain.review.dto;

import com.assu.server.domain.review.entity.ReviewPhoto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class ReviewRequestDTO {
    @Getter
    public static class WriteReviewRequestDTO {
        private String content;
        private Integer rate;
        //private List<ReviewPhoto> reviewImage;
        private Long storeId;
        private Long partnerId;
    }
}
