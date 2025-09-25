package com.assu.server.domain.report.service;

import com.assu.server.domain.report.dto.ReportRequestDTO;
import com.assu.server.domain.report.dto.ReportResponseDTO;

public interface ReportService {

    // 콘텐츠 신고 생성 (리뷰, 건의글)
    ReportResponseDTO.CreateReportResponse reportContent(Long reporterId, ReportRequestDTO.CreateContentReportRequest request);

    // 작성자 신고 생성 (리뷰/건의글 작성자)
    ReportResponseDTO.CreateReportResponse reportStudent(Long reporterId, ReportRequestDTO.CreateStudentReportRequest request);
}