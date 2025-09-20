package com.assu.server.domain.auth.service;

import com.assu.server.domain.auth.dto.verification.VerificationRequestDTO;

public interface VerificationService {
    void checkPhoneNumberAvailability(
            VerificationRequestDTO.PhoneVerificationCheckRequest request);

    void checkEmailAvailability(
            VerificationRequestDTO.EmailVerificationCheckRequest request);
}
