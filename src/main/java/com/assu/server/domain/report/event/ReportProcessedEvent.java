package com.assu.server.domain.report.event;

import com.assu.server.domain.report.entity.enums.ReportTargetType;
import com.assu.server.domain.report.entity.enums.ReportStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReportProcessedEvent {
    private final Long reportId;
    private final ReportTargetType targetType;
    private final Long targetId;
    private final ReportStatus status;
}
