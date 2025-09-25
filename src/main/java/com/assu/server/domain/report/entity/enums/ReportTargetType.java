package com.assu.server.domain.report.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportTargetType {
    STUDENT_USER("학생 사용자"),
    REVIEW("리뷰"),
    SUGGESTION("건의글");

    private final String description;
}
