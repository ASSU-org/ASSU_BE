package com.assu.server.domain.auth.service;

import com.assu.server.domain.auth.dto.verification.VerificationRequestDTO;

public interface EmailAuthService {

    void checkEmailAvailability(VerificationRequestDTO.EmailVerificationCheckRequest request);
}
