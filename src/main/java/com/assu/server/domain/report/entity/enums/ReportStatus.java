package com.assu.server.domain.report.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportStatus {
    PENDING("대기중"),
    PROCESSED("처리완료"),
    REJECTED("기각"),
    UNDER_REVIEW("검토중");

    private final String description;
}
