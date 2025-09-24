package com.assu.server.domain.common.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportedStatus {
    NORMAL("정상"),
    REPORTED("신고됨"),
    DELETED("삭제됨");

    private final String description;
}
