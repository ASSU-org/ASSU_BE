package com.assu.server.domain.review.service;

import com.assu.server.domain.review.dto.ReviewRequestDTO;
import com.assu.server.domain.review.dto.ReviewResponseDTO;
import org.springframework.web.bind.annotation.RequestBody;

public interface ReviewService {
    ReviewResponseDTO.WriteReviewResponseDTO writeReview(@RequestBody ReviewRequestDTO.WriteReviewRequestDTO request);

}
