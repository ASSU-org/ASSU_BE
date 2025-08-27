package com.assu.server.domain.admin.controller;

import com.assu.server.domain.admin.dto.AdminResponseDTO;
import com.assu.server.domain.admin.service.AdminService;
import com.assu.server.global.apiPayload.BaseResponse;
import com.assu.server.global.apiPayload.code.status.SuccessStatus;
import com.assu.server.global.util.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @Operation(
            summary = "제휴하지 않은 파트너를 추천하는 API 입니다.",
            description = "제휴하지 않은 파트너 중 한 곳을 랜덤으로 조회합니다."
    )
    @GetMapping("/partner-recommend")
    public BaseResponse<AdminResponseDTO.RandomPartnerResponseDTO> randomPartnerRecommend(
            @AuthenticationPrincipal PrincipalDetails pd
            ) {
        Long adminId = pd.getMember().getId();
        return BaseResponse.onSuccess(SuccessStatus._OK, adminService.suggestRandomPartner(adminId));
    }
}
