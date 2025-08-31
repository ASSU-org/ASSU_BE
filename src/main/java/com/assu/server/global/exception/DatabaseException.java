package com.assu.server.global.exception;


import com.assu.server.global.apiPayload.code.BaseErrorCode;

public class DatabaseException extends GeneralException {

    public DatabaseException(BaseErrorCode code)  {
        super(code);
    }
}
