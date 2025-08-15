package com.assu.server.domain.inquiry.service;

import com.assu.server.domain.inquiry.dto.InquiryCreateRequestDTO;
import com.assu.server.domain.inquiry.dto.InquiryResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;


public interface InquiryService {
    Long create(InquiryCreateRequestDTO req, Long memberId);
    Map<String, Object> getInquiries(String status, int page, int size, Long memberId);
    InquiryResponseDTO get(Long id, Long memberId);
    void answer(Long inquiryId, String answerText);
}
