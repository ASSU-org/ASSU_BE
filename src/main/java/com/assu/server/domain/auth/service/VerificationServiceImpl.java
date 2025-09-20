package com.assu.server.domain.auth.service;

import com.assu.server.domain.auth.dto.verification.VerificationRequestDTO;
import com.assu.server.domain.auth.exception.CustomAuthException;
import com.assu.server.domain.auth.repository.CommonAuthRepository;
import com.assu.server.domain.member.repository.MemberRepository;
import com.assu.server.global.apiPayload.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VerificationServiceImpl implements VerificationService {

    private final MemberRepository memberRepository;
    private final CommonAuthRepository commonAuthRepository;

    @Override
    public void checkPhoneNumberAvailability(
            VerificationRequestDTO.PhoneVerificationCheckRequest request) {

        boolean exists = memberRepository.existsByPhoneNum(request.getPhoneNumber());

        if (exists) {
            throw new CustomAuthException(ErrorStatus.EXISTED_PHONE);
        }
    }

    @Override
    public void checkEmailAvailability(
            VerificationRequestDTO.EmailVerificationCheckRequest request) {

        boolean exists = commonAuthRepository.existsByEmail(request.getEmail());

        if (exists) {
            throw new CustomAuthException(ErrorStatus.EXISTED_EMAIL);
        }
    }
}
