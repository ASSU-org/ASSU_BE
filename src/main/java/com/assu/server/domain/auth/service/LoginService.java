package com.assu.server.domain.auth.service;

import com.assu.server.domain.auth.dto.login.CommonLoginRequest;
import com.assu.server.domain.auth.dto.login.LoginResponse;
import com.assu.server.domain.auth.dto.login.RefreshResponse;
import com.assu.server.domain.auth.dto.login.StudentLoginRequest;

public interface LoginService {
    LoginResponse loginCommon(CommonLoginRequest request);
    LoginResponse loginStudent(StudentLoginRequest request);
    RefreshResponse refresh(String refreshToken);
}
