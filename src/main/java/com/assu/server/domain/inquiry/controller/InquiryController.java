package com.assu.server.domain.inquiry.controller;

import com.assu.server.domain.inquiry.dto.InquiryCreateRequestDTO;
import com.assu.server.domain.inquiry.dto.InquiryResponseDTO;
import com.assu.server.domain.inquiry.service.InquiryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/member/inquiries")
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryService inquiryService;

    @PostMapping
    public ResponseEntity<Long> create(
            @RequestBody @Valid InquiryCreateRequestDTO req,
            @RequestParam Long memberId
    ) {
        Long id = inquiryService.create(req, memberId);
        return ResponseEntity.ok(id);
    }

    @GetMapping
    public Page<InquiryResponseDTO> list(
            @RequestParam(defaultValue = "all") String status,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam Long memberId
    ) {
        if (!"all".equalsIgnoreCase(status)
                && !"waiting".equalsIgnoreCase(status)
                && !"answered".equalsIgnoreCase(status)) {
            throw new IllegalArgumentException("상태값: [all, waiting, answered]");
        }
        return inquiryService.list(status, pageable, memberId);
    }

    /** 단건 상세 조회*/
    @GetMapping("/{inquiry_id}")
    public InquiryResponseDTO get(
            @PathVariable Long id,
            @RequestParam Long memberId
    ) {
        return inquiryService.get(id, memberId);
    }

    /** 운영자: 답변 완료 처리 */
    @PatchMapping("/{inquiry_id}/answer")
    public ResponseEntity<Void> markAnswered(@PathVariable Long id) {
        inquiryService.markAnswered(id);
        return ResponseEntity.noContent().build();
    }
}
