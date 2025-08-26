package com.assu.server.domain.inquiry.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class InquiryAnswerRequestDTO {
    @NotBlank(message = "answer는 비어 있을 수 없습니다.")
    private String answer;
}
