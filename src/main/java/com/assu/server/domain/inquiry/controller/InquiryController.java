package com.assu.server.domain.inquiry.controller;

import com.assu.server.domain.inquiry.dto.InquiryAnswerRequestDTO;
import com.assu.server.domain.inquiry.dto.InquiryCreateRequestDTO;
import com.assu.server.domain.inquiry.dto.InquiryResponseDTO;
import com.assu.server.domain.inquiry.service.InquiryService;
import com.assu.server.global.apiPayload.BaseResponse;
import com.assu.server.global.apiPayload.code.status.SuccessStatus;

import com.assu.server.global.util.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Inquiry", description = "문의 API")
@RestController
@RequestMapping("/member/inquiries")
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryService inquiryService;

    @Operation(
            summary = "문의 생성 API",
            description = "# [v1.0 (2025-09-02)](https://www.notion.so/2441197c19ed800688f0cfb304dead63?source=copy_link)\n" +
                    "- 문의를 생성하고 해당 문의의 id를 반환합니다.\n"+
                    "  - InquiryCreateRequestDTO: title, content, email\n"
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
            description = "# [v1.0 (2025-09-02)](https://www.notion.so/2441197c19ed803eba4af9598484e5c5?source=copy_link)\n" +
                    "- 본인의 문의 목록을 상태별로 조회합니다.\n"+
                    "  - status: Request Param, String, [all/waiting/answered]\n" +
                    "  - page: Request Param, Integer, 1 이상\n" +
                    "  - size: Request Param, Integer, default = 20"
    )
    @GetMapping
    public BaseResponse<Map<String, Object>> list(
            @AuthenticationPrincipal PrincipalDetails pd,
            @RequestParam(defaultValue = "all") String status, // all | waiting | answered
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        Map<String, Object> response = inquiryService.getInquiries(status, page, size, pd.getId());
        return BaseResponse.onSuccess(SuccessStatus._OK, response);
    }

    /** 단건 상세 조회 */
    @Operation(
            summary = "문의 단건 상세 조회 API",
            description = "# [v1.0 (2025-09-02)](https://www.notion.so/24e1197c19ed800f8a1fffc5a101f3c0?source=copy_link)\n" +
                    "- 본인의 단건 문의를 상세 조회합니다.\n"+
                    "  - inquiry-id: Path Variable, Long\n"
    )
    @GetMapping("/{inquiry-id}")
    public BaseResponse<InquiryResponseDTO> get(
            @AuthenticationPrincipal PrincipalDetails pd,
            @PathVariable("inquiry-id") Long inquiryId
    ) {
        InquiryResponseDTO response = inquiryService.get(inquiryId, pd.getMemberId());
        return BaseResponse.onSuccess(SuccessStatus._OK, response);
    }

    /** 문의 답변 (운영자) */
    @Operation(
            summary = "운영자 답변 API",
            description = "# [v1.0 (2025-09-02)](https://www.notion.so/24e1197c19ed8064808fcca568b8912a?source=copy_link)\n" +
                    "- 문의에 답변을 저장하고 상태를 ANSWERED로 변경합니다.\n"+
                    "  - inquiry-id: Path Variable, Long\n"
    )
    @PatchMapping("/{inquiry-id}/answer")
    public BaseResponse<String> answer(
            @PathVariable("inquiry-id") Long inquiryId,
            @RequestBody @Valid InquiryAnswerRequestDTO req
    ) {
        inquiryService.answer(inquiryId, req.getAnswer());
        return BaseResponse.onSuccess(SuccessStatus._OK, "The inquiry answered successfully. id=" + inquiryId);
    }
}