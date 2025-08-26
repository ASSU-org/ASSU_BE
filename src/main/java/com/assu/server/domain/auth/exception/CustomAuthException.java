package com.assu.server.domain.auth.exception;

import com.assu.server.global.apiPayload.code.BaseErrorCode;
import com.assu.server.global.exception.GeneralException;

public class CustomAuthException extends GeneralException {

    public CustomAuthException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}