package com.assu.server.domain.review.controller;

import com.assu.server.domain.review.dto.ReviewRequestDTO;
import com.assu.server.domain.review.dto.ReviewResponseDTO;
import com.assu.server.domain.review.service.ReviewService;
import com.assu.server.global.apiPayload.BaseResponse;
import com.assu.server.global.apiPayload.code.status.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;
    @Operation(
            summary = "리뷰 작성 API입니다.",
            description = "리뷰 내용과 별점, 리뷰 이미지를 입력해주세요."
    )
    @PostMapping()
    public BaseResponse<ReviewResponseDTO.WriteReviewResponseDTO> writeReview(@RequestBody ReviewRequestDTO.WriteReviewRequestDTO writeReviewRequestDTO) {
        return BaseResponse.onSuccess(SuccessStatus._OK, reviewService.writeReview(writeReviewRequestDTO));
    }

    @Operation(
            summary = "내가 쓴 리뷰 조회 API입니다.",
            description = "Autorization 후에 사용해주세요."
    )
    @GetMapping("/student")
    public BaseResponse<List<ReviewResponseDTO.CheckStudentReviewResponseDTO>> checkStudent() {
        return BaseResponse.onSuccess(SuccessStatus._OK, reviewService.checkStudentReview());
    }

    @Operation(
            summary = "내가 쓴 리뷰 삭제 API입니다.",
            description = "삭제할 리뷰 ID를 입력해주세요."
    )
    @DeleteMapping("/{reviewId}")
    public BaseResponse<ReviewResponseDTO.DeleteReviewResponseDTO> deleteReview(@PathVariable Long reviewId) {
        return BaseResponse.onSuccess(SuccessStatus._OK, reviewService.deleteReview(reviewId));
    }

    @Operation(
            summary = "내 가게 리뷰 조회 API입니다.",
            description = "내 가게 ID를 입력해주세요."
    )
    @GetMapping("/partner")
    public BaseResponse<List<ReviewResponseDTO.CheckPartnerReviewResponseDTO>> checkPartnerReview(){
        return BaseResponse.onSuccess(SuccessStatus._OK, reviewService.checkPartnerReview());
    }
}
