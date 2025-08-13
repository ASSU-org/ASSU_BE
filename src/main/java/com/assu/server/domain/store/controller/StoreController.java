package com.assu.server.domain.store.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.assu.server.domain.store.dto.StoreResponseDTO;
import com.assu.server.global.apiPayload.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@Tag(name = "가게 관련 api", description = "가게와 관련된 api")
@RequiredArgsConstructor
public class StoreController {

	@GetMapping("/store/best")
	@Operation(summary = "홈화면의 현재 인기 매장 조회 api", description = "관리자, 사용자, 제휴업체 모두 사용하는 api")
	public ResponseEntity<BaseResponse<StoreResponseDTO.todayBest>> getTodayBestStore(){

	}
}
