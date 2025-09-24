package com.assu.server.domain.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ReportResponseDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateReportResponse {
        private Long reportId;

        public static CreateReportResponse of(Long reportId) {
            return CreateReportResponse.builder()
                    .reportId(reportId)
                    .build();
        }
    }
}
