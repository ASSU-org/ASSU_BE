package com.assu.server.infra.aligo.exception;

import com.assu.server.global.apiPayload.code.BaseErrorCode;
import com.assu.server.global.exception.GeneralException;

public class AligoException  extends GeneralException {

    public AligoException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
