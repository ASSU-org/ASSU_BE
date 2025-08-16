package com.assu.server.domain.deviceToken.controller;

import com.assu.server.domain.common.entity.Member;
import com.assu.server.domain.deviceToken.dto.DeviceTokenRequest;
import com.assu.server.domain.deviceToken.service.DeviceTokenService;
import com.assu.server.global.apiPayload.BaseResponse;
import com.assu.server.global.apiPayload.code.status.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("deviceTokens")
@RequiredArgsConstructor
public class DeviceTokenController {
    private final DeviceTokenService service;
    @Operation(
            summary = "device Token 등록 API",
            description = "멤버 아이디와 fcm Token을 보내주세요."
    )
    @PostMapping("/register")
    public BaseResponse<String> register(@RequestBody DeviceTokenRequest req,
                                         @RequestParam Long memberId) {
        service.register(req.getToken(), memberId);
        return BaseResponse.onSuccess(
                SuccessStatus._OK,
                "Device token registered successfully. memberId=" + memberId
        );
    }

    @Operation(
            summary = "device Token 등록 해제 API",
            description = "로그아웃, 회원 탈퇴시 호출하시면 됩니다. 멤버의 tokenId를 보내주세요!"
    )
    @DeleteMapping("/unregister/{tokenId}")
    public BaseResponse<String> unregister(@PathVariable Long tokenId) {
        service.unregister(tokenId);
        return BaseResponse.onSuccess(
                SuccessStatus._OK,
                "Device token unregistered successfully. tokenId=" + tokenId
        );
    }
}
