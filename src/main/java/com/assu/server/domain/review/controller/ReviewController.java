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
@RequestMapping("/review")
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
    @GetMapping("/{studentId}")
    public BaseResponse<List<ReviewResponseDTO.CheckStudentReviewResponseDTO>> checkStudent() {
        return BaseResponse.onSuccess(SuccessStatus._OK, reviewService.checkStudentReview());
    }
}
