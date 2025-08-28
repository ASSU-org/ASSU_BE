package com.assu.server.domain.inquiry.dto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor @Builder
public class InquiryCreateRequestDTO {
    private String title;
    private String content;
    private String email;
}
