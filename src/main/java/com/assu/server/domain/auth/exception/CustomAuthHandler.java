package com.assu.server.domain.auth.exception;

import com.assu.server.global.apiPayload.code.BaseErrorCode;
import com.assu.server.global.exception.GeneralException;
import org.springframework.http.HttpStatus;

public class CustomAuthHandler extends GeneralException {

    public CustomAuthHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}