package com.assu.server.domain.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.assu.server.domain.member.entity.Member;
import com.assu.server.domain.user.dto.StudentResponseDTO;
import com.assu.server.global.apiPayload.BaseResponse;
import com.assu.server.global.util.PrincipalDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import com.assu.server.domain.user.dto.StudentResponseDTO;
import com.assu.server.domain.user.service.StudentService;
import com.assu.server.global.apiPayload.BaseResponse;
import com.assu.server.global.apiPayload.code.status.SuccessStatus;
import org.springframework.web.bind.annotation.*;
@RestController
@Tag(name = "유저 관련 api", description = "유저와 관련된 로직을 처리하는 api")
@RequiredArgsConstructor
@RequestMapping("/user")
public class StudentController {

	private final StudentService studentService;

	@GetMapping("/partnership/{year}/{month}")
	@Operation(summary = "유저의 제휴 내역을 조회", description = "건수 및 금액으로 조회")
	public ResponseEntity<BaseResponse<StudentResponseDTO.myPartnership>> getMyPartnership(
		@PathVariable int year, @PathVariable int month, @AuthenticationPrincipal PrincipalDetails pd
	){
		StudentResponseDTO.myPartnership result = studentService.getMyPartnership(pd.getId(), year, month);

		return ResponseEntity.ok(BaseResponse.onSuccess(SuccessStatus.PARTNERSHIP_HISTORY_SUCCESS, result));
	}




    @Operation(
            summary = "스탬프 조회 API",
            description = "Authorization 후에 사용해주세요."
    )
    @GetMapping("/stamp")
    public BaseResponse<StudentResponseDTO.CheckStampResponseDTO> getStamp(
            @AuthenticationPrincipal PrincipalDetails pd
    ) {
        return BaseResponse.onSuccess(SuccessStatus._OK, studentService.getStamp(pd.getId()));
    }
}
