package com.assu.server.domain.review.controller;

import com.assu.server.domain.review.dto.ReviewRequestDTO;
import com.assu.server.domain.review.dto.ReviewResponseDTO;
import com.assu.server.domain.review.service.ReviewService;
import com.assu.server.global.apiPayload.BaseResponse;
import com.assu.server.global.apiPayload.code.status.SuccessStatus;
import com.assu.server.global.util.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;
    @Operation(
            summary = "리뷰 작성 API",
            description = "리뷰 내용과 별점, 리뷰 이미지를 입력해주세요."

    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse<ReviewResponseDTO.WriteReviewResponseDTO> writeReview(
            @AuthenticationPrincipal PrincipalDetails pd,
            @RequestPart("request") ReviewRequestDTO.WriteReviewRequestDTO request,
            @RequestPart(value = "reviewImages", required = false) List<MultipartFile> reviewImages
    ) {
        return BaseResponse.onSuccess(SuccessStatus._OK, reviewService.writeReview(request, pd.getId(), reviewImages));
    }

    @Operation(
            summary = "내가 쓴 리뷰 조회 API",
            description = "Authorization 후에 사용해주세요."
    )
    @GetMapping("/student")
    public BaseResponse<List<ReviewResponseDTO.CheckStudentReviewResponseDTO>> checkStudent(
            @AuthenticationPrincipal PrincipalDetails pd
    ) {
        return BaseResponse.onSuccess(SuccessStatus._OK, reviewService.checkStudentReview(pd.getId()));
    }

    @Operation(
            summary = "내가 쓴 리뷰 삭제 API",
            description = "삭제할 리뷰 ID를 입력해주세요."
    )
    @DeleteMapping("/{reviewId}")
    public BaseResponse<ReviewResponseDTO.DeleteReviewResponseDTO> deleteReview(
            @AuthenticationPrincipal PrincipalDetails pd,
            @PathVariable Long reviewId) {
        Long memberId = pd.getMember().getId();

        return BaseResponse.onSuccess(SuccessStatus._OK, reviewService.deleteReview(reviewId));
    }

    @Operation(
            summary = "내 가게 리뷰 조회 API",
            description = "Authorization 후에 사용해주세요."
    )
    @GetMapping("/partner")
    public BaseResponse<List<ReviewResponseDTO.CheckPartnerReviewResponseDTO>> checkPartnerReview(
            @AuthenticationPrincipal PrincipalDetails pd
    ){
        return BaseResponse.onSuccess(SuccessStatus._OK, reviewService.checkPartnerReview(pd.getId()));
    }
}
