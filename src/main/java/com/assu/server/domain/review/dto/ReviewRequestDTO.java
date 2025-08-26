package com.assu.server.domain.review.dto;

import com.assu.server.domain.review.entity.ReviewPhoto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class    ReviewRequestDTO {
    @Getter
    public static class WriteReviewRequestDTO {
        private String content;
        private Integer rate;
        private List<MultipartFile> reviewImage;
        private Long storeId;
        private Long partnerId;
    }
}
