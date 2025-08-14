package com.assu.server.domain.auth.controller;

import com.assu.server.domain.auth.dto.AuthRequestDTO;
import com.assu.server.domain.auth.service.PhoneAuthService;
import com.assu.server.global.apiPayload.BaseResponse;
import com.assu.server.global.apiPayload.code.status.SuccessStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final PhoneAuthService phoneAuthService;

    @PostMapping("/phone-numbers/send")
    public BaseResponse<Void> sendAuthNumber(
            @RequestBody @Valid AuthRequestDTO.PhoneAuthSendRequest request
    ) {
        phoneAuthService.sendAuthNumber(request.getPhoneNumber());
        return BaseResponse.onSuccess(SuccessStatus.SEND_AUTH_NUMBER_SUCCESS, null);
    }

    @PostMapping("/phone-numbers/verify")
    public BaseResponse<Void> checkAuthNumber(
            @RequestBody @Valid AuthRequestDTO.PhoneAuthVerifyRequest request
    ) {
        phoneAuthService.verifyAuthNumber(
                request.getPhoneNumber(),
                request.getAuthNumber()
        );
        return BaseResponse.onSuccess(SuccessStatus.VERIFY_AUTH_NUMBER_SUCCESS, null);
    }
}