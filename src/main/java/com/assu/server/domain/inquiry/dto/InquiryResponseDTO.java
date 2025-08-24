package com.assu.server.domain.inquiry.dto;
import com.assu.server.domain.inquiry.entity.Inquiry;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InquiryResponseDTO {
    private Long id;
    private String title;
    private String content;
    private String email;
    private String status;
    private String answer;
    private LocalDateTime createdAt;

    public static InquiryResponseDTO from(Inquiry inquiry) {
        return InquiryResponseDTO.builder()
                .id(inquiry.getId())
                .title(inquiry.getTitle())
                .content(inquiry.getContent())
                .email(inquiry.getEmail())
                .status(inquiry.getStatus().name())
                .answer(inquiry.getAnswer())
                .createdAt(inquiry.getCreatedAt())
                .build();
    }
}