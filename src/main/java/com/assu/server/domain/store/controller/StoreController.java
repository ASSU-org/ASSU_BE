package com.assu.server.domain.store.controller;

import com.assu.server.domain.review.dto.ReviewResponseDTO;
import com.assu.server.domain.store.dto.StoreResponseDTO;
import com.assu.server.domain.store.service.StoreService;
import com.assu.server.global.apiPayload.BaseResponse;
import com.assu.server.global.apiPayload.code.status.SuccessStatus;
import com.assu.server.global.util.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
@RequiredArgsConstructor
@RequestMapping("/store")
public class StoreController {

    private final StoreService storeService;

    @Operation(
            summary = "내 가게 순위 조회 API",
            description = "partnerId로 접근해주세요."
    )
    @GetMapping("/ranking")
    public BaseResponse<StoreResponseDTO.WeeklyRankResponseDTO> getWeeklyRank(
            @AuthenticationPrincipal PrincipalDetails pd
            ) {
        Long memberId = pd.getMember().getId();
        return BaseResponse.onSuccess(SuccessStatus._OK, storeService.getWeeklyRank(memberId));
    }

    @Operation(
            summary = "내 가게 순위 6주치 조회 API",
            description = "partnerId로 접근해주세요"
    )
    @GetMapping("/ranking/weekly")
    public BaseResponse<List<StoreResponseDTO.WeeklyRankResponseDTO>> getWeeklyRankByPartnerId(
            @AuthenticationPrincipal PrincipalDetails pd
    ){
        Long memberId = pd.getMember().getId();
        return BaseResponse.onSuccess(SuccessStatus._OK, storeService.getListWeeklyRank(memberId).getItems());
    }
}
