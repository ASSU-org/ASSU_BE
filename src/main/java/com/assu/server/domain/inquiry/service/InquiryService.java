package com.assu.server.domain.inquiry.service;

import com.assu.server.domain.inquiry.dto.InquiryCreateRequestDTO;
import com.assu.server.domain.inquiry.dto.InquiryResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InquiryService {
    Long create(InquiryCreateRequestDTO req, Long memberId);
    Page<InquiryResponseDTO> list(String status, Pageable pageable, Long memberId);
    InquiryResponseDTO get(Long id, Long memberId);
    void markAnswered(Long id);
}
