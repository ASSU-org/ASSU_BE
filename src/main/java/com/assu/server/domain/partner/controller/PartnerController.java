package com.assu.server.domain.partner.controller;

import com.assu.server.domain.partner.dto.PartnerResponseDTO;
import com.assu.server.domain.partner.service.PartnerService;
import com.assu.server.global.apiPayload.BaseResponse;
import com.assu.server.global.apiPayload.code.status.SuccessStatus;
import com.assu.server.global.util.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/partner")
@RequiredArgsConstructor
public class PartnerController {

    private final PartnerService partnerService;

    @Operation(
            summary = "어드민 추천 API",
            description = "제휴하지 않은 어드민 중 두 곳을 랜덤으로 조회합니다."
    )
    @GetMapping("/admin-recommend")
    public BaseResponse<PartnerResponseDTO.RandomAdminResponseDTO> randomAdminRecommend(
            @AuthenticationPrincipal PrincipalDetails pd
            ){
        Long partnerId = pd.getMember().getId();
        return BaseResponse.onSuccess(SuccessStatus._OK, partnerService.getRandomAdmin(partnerId));
    }
}
