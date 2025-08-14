package com.assu.server.domain.auth.exception;

import com.assu.server.global.apiPayload.code.BaseErrorCode;
import com.assu.server.global.exception.exception.GeneralException;

public class AuthException extends GeneralException {

    public AuthException(BaseErrorCode code) {
        super(code);
    }
}