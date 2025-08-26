package com.assu.server.domain.review.service;

import com.assu.server.domain.review.dto.ReviewRequestDTO;
import com.assu.server.domain.review.dto.ReviewResponseDTO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface ReviewService {
    ReviewResponseDTO.WriteReviewResponseDTO writeReview(@RequestBody ReviewRequestDTO.WriteReviewRequestDTO request, Long memberId);
    List<ReviewResponseDTO.CheckStudentReviewResponseDTO> checkStudentReview(Long memberId);
    List<ReviewResponseDTO.CheckPartnerReviewResponseDTO> checkPartnerReview(Long memberId);
    ReviewResponseDTO.DeleteReviewResponseDTO deleteReview(@PathVariable Long reviewId);
}
