package com.assu.server.domain.auth.service;

import com.assu.server.domain.auth.dto.login.LoginRequest;
import com.assu.server.domain.auth.dto.login.LoginResponse;
import com.assu.server.domain.auth.dto.login.RefreshResponse;
import com.assu.server.domain.auth.dto.login.StudentLoginRequest;

public interface LoginService {
    LoginResponse login(LoginRequest request);
    LoginResponse loginStudent(StudentLoginRequest request);
    RefreshResponse refresh(String refreshToken);
}
