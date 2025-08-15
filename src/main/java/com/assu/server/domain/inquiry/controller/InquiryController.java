package com.assu.server.domain.inquiry.controller;

import com.assu.server.domain.inquiry.dto.InquiryAnswerRequestDTO;
import com.assu.server.domain.inquiry.dto.InquiryCreateRequestDTO;
import com.assu.server.domain.inquiry.dto.InquiryResponseDTO;
import com.assu.server.domain.inquiry.service.InquiryService;
import com.assu.server.global.apiPayload.BaseResponse;
import com.assu.server.global.apiPayload.code.status.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/member/inquiries")
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryService inquiryService;

    @Operation(
            summary = "문의를 생성하는 API입니다.",
            description = "셍성 성공시 생성된 문의의 ID를 반환합니다."
    )
    @PostMapping
    public BaseResponse<Long> create(
            @RequestBody @Valid InquiryCreateRequestDTO req,
            @RequestParam Long memberId
    ) {
        Long id = inquiryService.create(req, memberId);
        return BaseResponse.onSuccess(SuccessStatus._OK, id);
    }

    @Operation(
            summary = "문의 목록을 조회하는 API 입니다.",
            description = "page는 1 이상이어야 합니다."
    )
    @GetMapping
    public BaseResponse<Map<String, Object>> list(
            @RequestParam(defaultValue = "all") String status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam Long memberId
    ) {
        Map<String, Object> response = inquiryService.getInquiries(status, page, size, memberId);
        return BaseResponse.onSuccess(SuccessStatus._OK, response);
    }

    /** 단건 상세 조회*/
    @Operation(
            summary = "문의 단건 상세 조회 API 입니다.",
            description = "문의 ID를 보내주세요."
    )
    @GetMapping("/{inquiryId}")
    public BaseResponse<InquiryResponseDTO> get(
            @PathVariable("inquiryId") Long inquiryId,
            @RequestParam Long memberId
    ) {
        InquiryResponseDTO response = inquiryService.get(inquiryId, memberId);
        return BaseResponse.onSuccess(SuccessStatus._OK, response);
    }

    /** 문의 답변*/
    @Operation(
            summary = "운영자 답변 API입니다.",
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
