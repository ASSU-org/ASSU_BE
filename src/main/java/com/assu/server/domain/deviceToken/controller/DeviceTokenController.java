package com.assu.server.domain.deviceToken.controller;

import com.assu.server.domain.deviceToken.dto.DeviceTokenRequest;
import com.assu.server.domain.deviceToken.service.DeviceTokenService;
import com.assu.server.global.apiPayload.BaseResponse;
import com.assu.server.global.apiPayload.code.status.SuccessStatus;
import com.assu.server.global.util.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("deviceTokens")
@RequiredArgsConstructor
public class DeviceTokenController {

    private final DeviceTokenService service;

    @Operation(
            summary = "Device Token 등록 API",
            description = "로그인 사용자 기준으로 FCM Device Token을 등록합니다."
    )
    @PostMapping("/register")
    public BaseResponse<String> register(@AuthenticationPrincipal PrincipalDetails pd,
                                         @Valid @RequestBody DeviceTokenRequest req) {
        service.register(req.getToken(), pd.getId());
        return BaseResponse.onSuccess(
                SuccessStatus._OK,
                "Device token registered successfully. memberId=" + pd.getId()
        );
    }

    @Operation(
            summary = "Device Token 등록 해제 API",
            description = "로그아웃/탈퇴 시 호출합니다. 자신의 토큰만 해제됩니다."
    )
    @DeleteMapping("/unregister/{tokenId}")
    public BaseResponse<String> unregister(@AuthenticationPrincipal PrincipalDetails pd,
                                           @PathVariable Long tokenId) {
        service.unregister(tokenId, pd.getId()); // 소유자 검증을 서비스에서 수행하도록 memberId 전달
        return BaseResponse.onSuccess(
                SuccessStatus._OK,
                "Device token unregistered successfully. tokenId=" + tokenId
        );
    }
}