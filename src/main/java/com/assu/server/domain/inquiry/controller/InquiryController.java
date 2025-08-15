package com.assu.server.domain.inquiry.controller;

import com.assu.server.domain.inquiry.dto.InquiryCreateRequestDTO;
import com.assu.server.domain.inquiry.dto.InquiryResponseDTO;
import com.assu.server.domain.inquiry.service.InquiryService;
import com.assu.server.global.apiPayload.BaseResponse;
import com.assu.server.global.apiPayload.code.status.SuccessStatus;
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

    @PostMapping
    public BaseResponse<Long> create(
            @RequestBody @Valid InquiryCreateRequestDTO req,
            @RequestParam Long memberId
    ) {
        Long id = inquiryService.create(req, memberId);
        return BaseResponse.onSuccess(SuccessStatus._OK, id);
    }

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
    @GetMapping("/{inquiryId}")
    public BaseResponse<InquiryResponseDTO> get(
            @PathVariable("inquiryId") Long inquiryId,
            @RequestParam Long memberId
    ) {
        InquiryResponseDTO response = inquiryService.get(inquiryId, memberId);
        return BaseResponse.onSuccess(SuccessStatus._OK, response);
    }

    /** 운영자: 답변 완료 처리 */
    @PatchMapping("/{inquiryId}/answer")
    public BaseResponse<Void> markAnswered(@PathVariable Long inquiryId) {
        inquiryService.markAnswered(inquiryId);
        return BaseResponse.onSuccess(SuccessStatus._OK, null);
    }
}
