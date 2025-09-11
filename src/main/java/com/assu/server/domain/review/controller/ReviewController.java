package com.assu.server.domain.review.controller;

import com.assu.server.domain.review.dto.ReviewRequestDTO;
import com.assu.server.domain.review.dto.ReviewResponseDTO;
import com.assu.server.domain.review.service.ReviewService;
import com.assu.server.global.apiPayload.BaseResponse;
import com.assu.server.global.apiPayload.code.status.SuccessStatus;
import com.assu.server.global.util.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    public BaseResponse<Page<ReviewResponseDTO.CheckReviewResponseDTO>> checkStudent(
            @AuthenticationPrincipal PrincipalDetails pd, Pageable pageable
    ) {
        return BaseResponse.onSuccess(SuccessStatus._OK, reviewService.checkStudentReview(pd.getId(), pageable));
    }

    @Operation(
        summary = "내 가게 리뷰 조회 API",
        description = "Authorization 후에 사용해주세요."
    )
    @GetMapping("/partner")
    public BaseResponse<Page<ReviewResponseDTO.CheckReviewResponseDTO>> checkPartnerReview(
        @AuthenticationPrincipal PrincipalDetails pd, Pageable pageable
    ){
        return BaseResponse.onSuccess(SuccessStatus._OK, reviewService.checkPartnerReview(pd.getId(), pageable));
    }

    @Operation(
        summary = "가게 리뷰 조회 API",
        description = "storeId 기반으로 가게 리뷰를 조회하는 API 입니다."
    )
    @GetMapping("/store/{storeId}")
    public BaseResponse<Page<ReviewResponseDTO.CheckReviewResponseDTO>> checkStoreReview(
        Pageable pageable, @PathVariable Long storeId
    ){
        return BaseResponse.onSuccess(SuccessStatus._OK, reviewService.checkStoreReview(storeId, pageable));
    }

    @Operation(
            summary = "내가 쓴 리뷰 삭제 API",
            description = "삭제할 리뷰 ID를 입력해주세요."
    )
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<BaseResponse<ReviewResponseDTO.DeleteReviewResponseDTO>> deleteReview(
            @PathVariable Long reviewId) {

        return ResponseEntity.ok(BaseResponse.onSuccess(SuccessStatus._OK, reviewService.deleteReview(reviewId)));
    }

    @Operation(
        summary = "store 리뷰 평균 조회 API",
        description = "storeId 기반으로 조회하는 API 입니다."
    )
    @GetMapping("/average/{storeId}")
    public ResponseEntity<BaseResponse<ReviewResponseDTO.StandardScoreResponseDTO>> getStandardScore(
        @PathVariable Long storeId
    ){
        return ResponseEntity.ok(BaseResponse.onSuccess(SuccessStatus._OK, reviewService.standardScore(storeId)));
    }

    @Operation(
        summary = "store 리뷰 평균 조회 API",
        description = "partner 로그인 시 자신의 가게 평균을 조회하는 api 입니다."
    )
    @GetMapping("/average")
    public ResponseEntity<BaseResponse<ReviewResponseDTO.StandardScoreResponseDTO>> getMyStoreAverage(
        @AuthenticationPrincipal PrincipalDetails pd
    ){
        return ResponseEntity.ok(BaseResponse.onSuccess(SuccessStatus._OK, reviewService.myStoreAverage(pd.getId())));
    }


}
