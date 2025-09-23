package com.assu.server.domain.report.controller;

import com.assu.server.domain.report.dto.ReportRequestDTO;
import com.assu.server.domain.report.dto.ReportResponseDTO;
import com.assu.server.domain.report.entity.enums.ReportTargetType;
import com.assu.server.domain.report.service.ReportService;
import com.assu.server.global.apiPayload.BaseResponse;
import com.assu.server.global.apiPayload.code.status.SuccessStatus;
import com.assu.server.global.util.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Report", description = "신고 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    @Operation(summary = "콘텐츠 신고 API",
            description = "# [v1.0 (2025-09-24)]()\n" +
            "- 신고자는 본인 Member ID로 자동 설정됩니다.\n" +
            "- 자기 자신의 콘텐츠를 신고할 수 없습니다.\n" +
            "- 동일한 대상을 중복 신고할 수 없습니다.\n\n" +
            "**Request Body:**\n" +
            "- `targetType` (String, required): 신고 대상 타입 (REVIEW, SUGGESTION)\n" +
            "- `targetId` (Long, required): 리뷰 ID 또는 건의글 ID\n" +
            "- `reportType` (String, required): 신고 유형\n" +
            "  - 리뷰 신고: REVIEW_INAPPROPRIATE_CONTENT, REVIEW_FALSE_INFORMATION, REVIEW_SPAM\n" +
            "  - 건의글 신고: SUGGESTION_INAPPROPRIATE_CONTENT, SUGGESTION_FALSE_INFORMATION, SUGGESTION_SPAM\n\n" +
            "**Response:**\n" +
            "- 성공 시 201(CREATED)과 신고 ID 반환")
    @PostMapping
    public BaseResponse<ReportResponseDTO.CreateReportResponse> reportContent(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody @Valid ReportRequestDTO.CreateContentReportRequest request
    ) {
        Long reporterId = principalDetails.getMember().getId();
        ReportResponseDTO.CreateReportResponse response = reportService.reportContent(reporterId, request);
        return BaseResponse.onSuccess(SuccessStatus.REPORT_SUCCESS, response);
    }

    @Operation(summary = "작성자 신고 API",
            description = "# [v1.0 (2025-09-24)]()\n" +
            "- 신고자는 본인 Member ID로 자동 설정됩니다.\n" +
            "- 자기 자신을 신고할 수 없습니다.\n" +
            "- 동일한 작성자를 중복 신고할 수 없습니다.\n\n" +
            "**Request Body:**\n" +
            "- `targetType` (String, required): 신고 대상 타입 (REVIEW, SUGGESTION)\n" +
            "- `targetId` (Long, required): 리뷰 ID 또는 건의글 ID\n" +
            "- `reportType` (String, required): 신고 유형\n" +
            "  - 사용자 신고: USER_SPAM, USER_INAPPROPRIATE_CONTENT, USER_HARASSMENT, USER_FRAUD, USER_PRIVACY_VIOLATION, USER_OTHER\n\n"
            +
            "**Response:**\n" +
            "- 성공 시 201(CREATED)과 신고 ID 반환")
    @PostMapping("/students")
    public BaseResponse<ReportResponseDTO.CreateReportResponse> reportStudent(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody @Valid ReportRequestDTO.CreateStudentReportRequest request
    ) {
        Long reporterId = principalDetails.getMember().getId();
        ReportResponseDTO.CreateReportResponse response = reportService.reportStudent(reporterId, request);
        return BaseResponse.onSuccess(SuccessStatus.REPORT_SUCCESS, response);
    }
}