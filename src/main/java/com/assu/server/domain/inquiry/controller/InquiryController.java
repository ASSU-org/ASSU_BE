package com.assu.server.domain.inquiry.controller;

import com.assu.server.domain.inquiry.dto.InquiryAnswerRequestDTO;
import com.assu.server.domain.inquiry.dto.InquiryCreateRequestDTO;
import com.assu.server.domain.inquiry.dto.InquiryResponseDTO;
import com.assu.server.domain.inquiry.service.InquiryService;
import com.assu.server.global.apiPayload.BaseResponse;
import com.assu.server.global.apiPayload.code.status.SuccessStatus;

import com.assu.server.global.util.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/member/inquiries")
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryService inquiryService;

    @Operation(
            summary = "문의를 생성하는 API",
            description = "생성 성공시 생성된 문의의 ID를 반환합니다."
    )
    @PostMapping
    public BaseResponse<Long> create(
            @AuthenticationPrincipal PrincipalDetails pd,
            @RequestBody @Valid InquiryCreateRequestDTO req
    ) {
        Long id = inquiryService.create(req, pd.getId());
        return BaseResponse.onSuccess(SuccessStatus._OK, id);
    }

    @Operation(
            summary = "문의 목록을 조회하는 API",
            description = "page는 1 이상이어야 합니다."
    )
    @GetMapping
    public BaseResponse<Map<String, Object>> list(
            @AuthenticationPrincipal PrincipalDetails pd,
            @RequestParam(defaultValue = "all") String status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        Map<String, Object> response = inquiryService.getInquiries(status, page, size, pd.getId());
        return BaseResponse.onSuccess(SuccessStatus._OK, response);
    }

    /** 단건 상세 조회 */
    @Operation(
            summary = "문의 단건 상세 조회 API",
            description = "문의 ID를 보내주세요."
    )
    @GetMapping("/{inquiryId}")
    public BaseResponse<InquiryResponseDTO> get(
            @AuthenticationPrincipal PrincipalDetails pd,
            @PathVariable("inquiryId") Long inquiryId
    ) {
        InquiryResponseDTO response = inquiryService.get(inquiryId, pd.getMemberId());
        return BaseResponse.onSuccess(SuccessStatus._OK, response);
    }

    /** 문의 답변 (운영자) */
    @Operation(
            summary = "운영자 답변 API",
            description = "문의에 답변을 저장하고 상태를 ANSWERED로 변경합니다."
    )
    @PatchMapping("/{inquiryId}/answer")
    public BaseResponse<Void> answer(
            @PathVariable Long inquiryId,
            @RequestBody @Valid InquiryAnswerRequestDTO req
    ) {
        inquiryService.answer(inquiryId, req.getAnswer());
        return BaseResponse.onSuccess(SuccessStatus._OK, null);
    }
}