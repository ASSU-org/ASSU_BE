package com.assu.server.domain.report.dto;

import com.assu.server.domain.report.entity.enums.ReportTargetType;
import com.assu.server.domain.report.entity.enums.ReportType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ReportRequestDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateContentReportRequest {
        @NotNull(message = "신고 대상 타입은 필수입니다.")
        private ReportTargetType targetType; // REVIEW, SUGGESTION

        @NotNull(message = "신고 대상 ID는 필수입니다.")
        private Long targetId; // 리뷰 ID 또는 건의글 ID

        @NotNull(message = "신고 유형은 필수입니다.")
        private ReportType reportType; // REVIEW_*, SUGGESTION_*
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateStudentReportRequest {
        @NotNull(message = "신고 대상의 작성 컨텐츠의 타입은 필수입니다.")
        private ReportTargetType targetType; // REVIEW, SUGGESTION

        @NotNull(message = "신고 대상의 작성 컨텐츠 ID는 필수입니다.")
        private Long targetId; // 리뷰 ID 또는 건의글 ID

        @NotNull(message = "유저 신고 유형은 필수입니다.")
        private ReportType reportType; // USER_*
    }
}