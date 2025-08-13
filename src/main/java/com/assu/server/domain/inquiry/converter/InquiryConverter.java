package com.assu.server.domain.inquiry.converter;

import com.assu.server.domain.inquiry.dto.InquiryResponseDTO;
import com.assu.server.domain.inquiry.entity.Inquiry;

public class InquiryConverter {
    public InquiryResponseDTO toDto(Inquiry i) {
        return InquiryResponseDTO.builder()
                .id(i.getId())
                .title(i.getTitle())
                .content(i.getContent())
                .email(i.getEmail())
                .status(i.getStatus().name())
                .createdAt(i.getCreatedAt())
                .build();
    }
}
