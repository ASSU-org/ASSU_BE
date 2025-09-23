package com.assu.server.domain.report.exception;

import com.assu.server.global.apiPayload.code.BaseErrorCode;
import com.assu.server.global.exception.GeneralException;

public class ReportException extends GeneralException {
    public ReportException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
