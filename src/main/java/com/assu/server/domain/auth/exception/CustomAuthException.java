package com.assu.server.domain.auth.exception;

import com.assu.server.global.apiPayload.code.BaseErrorCode;
import org.springframework.http.HttpStatus;

public class CustomAuthException extends RuntimeException {

    private final BaseErrorCode errorCode;
    private final String code;
    private final HttpStatus httpStatus;

    public CustomAuthException(BaseErrorCode errorCode) {
        super(errorCode.getReasonHttpStatus().getMessage());
        this.errorCode = errorCode;
        this.code = errorCode.getReasonHttpStatus().getCode();
        this.httpStatus = errorCode.getReasonHttpStatus().getHttpStatus();
    }

    public BaseErrorCode getErrorCode() {
        return errorCode;
    }

    public String getCode() {
        return code;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}