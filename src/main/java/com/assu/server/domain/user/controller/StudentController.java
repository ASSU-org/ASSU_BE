package com.assu.server.domain.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.assu.server.domain.user.dto.StudentResponseDTO;
import com.assu.server.global.apiPayload.BaseResponse;
import com.assu.server.global.util.PrincipalDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@Tag(name = "유저 관련 api", description = "유저와 관련된 로직을 처리하는 api")
@RequiredArgsConstructor
public class StudentController {

	@GetMapping("/partnership/{year}/{month}")
	@Operation(summary = "유저의 제휴 내역을 조회", description = "건수 및 금액으로 조회")
	public ResponseEntity<BaseResponse<StudentResponseDTO.myPartnership>> getMyPartnership(
		@PathVariable int year, @PathVariable int month, @AuthenticationPrincipal PrincipalDetails userDetails
	){

	}
}
