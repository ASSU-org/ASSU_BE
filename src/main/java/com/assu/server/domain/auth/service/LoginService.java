package com.assu.server.domain.auth.service;

import com.assu.server.domain.auth.dto.login.CommonLoginRequest;
import com.assu.server.domain.auth.dto.login.LoginResponse;
import com.assu.server.domain.auth.dto.login.RefreshResponse;
import com.assu.server.domain.auth.dto.signup.student.StudentTokenAuthPayload;

public interface LoginService {
    LoginResponse loginCommon(CommonLoginRequest request);
    LoginResponse loginSsuStudent(StudentTokenAuthPayload request);
    RefreshResponse refresh(String refreshToken);
}
