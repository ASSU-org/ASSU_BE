package com.assu.server.domain.store.controller;

import java.awt.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.assu.server.domain.store.dto.StoreResponseDTO;
import com.assu.server.domain.store.service.StoreService;
import com.assu.server.global.apiPayload.BaseResponse;
import com.assu.server.global.apiPayload.code.status.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import com.assu.server.global.util.PrincipalDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;
@RestController
@RequiredArgsConstructor
@Tag(name = "가게 관련 api", description = "가게와 관련된 api")
@RequestMapping("/store")
public class StoreController {

    private final StoreService storeService;

	@GetMapping("/best")
	@Operation(summary = "홈화면의 현재 인기 매장 조회 api", description = "관리자, 사용자, 제휴업체 모두 사용하는 api")
	public ResponseEntity<BaseResponse<StoreResponseDTO.todayBest>> getTodayBestStore() {
		StoreResponseDTO.todayBest result = storeService.getTodayBestStore();
		return ResponseEntity.ok(BaseResponse.onSuccess(SuccessStatus.BEST_STORE_SUCCESS, result));
	}


    @Operation(
            summary = "내 가게 순위 조회 API",
            description = "partnerId로 접근해주세요."
    )
    @GetMapping("/ranking")
    public ResponseEntity<BaseResponse<StoreResponseDTO.WeeklyRankResponseDTO>> getWeeklyRank(
            @AuthenticationPrincipal PrincipalDetails pd) {
        return ResponseEntity.ok(BaseResponse.onSuccess(SuccessStatus._OK, storeService.getWeeklyRank(pd.getId())));
    }

    @Operation(
            summary = "내 가게 순위 6주치 조회 API",
            description = "partnerId로 접근해주세요"
    )
    @GetMapping("/ranking/weekly")
    public BaseResponse<List<StoreResponseDTO.WeeklyRankResponseDTO>> getWeeklyRankByPartnerId(
            @AuthenticationPrincipal PrincipalDetails pd
    ){
        return BaseResponse.onSuccess(SuccessStatus._OK, storeService.getListWeeklyRank(pd.getId()).getItems());
    }


}
