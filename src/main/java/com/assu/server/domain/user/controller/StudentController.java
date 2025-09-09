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
@RequestMapping("/students")
public class StudentController {

	private final StudentService studentService;

	@GetMapping("/partnerships/{year}/{month}")
	@Operation(
		summary = "월별 제휴 사용내역 조회 API",
		description = "# [v1.0 (2025-09-09)](https://www.notion.so/_-2241197c19ed8134bd49d8841e841634?source=copy_link)\n" +
			"- `multipart/form-data`로 호출합니다.\n" +
			"- 처리: 정보 바탕으로 sessionManager에 session생성\n" +
			"- 성공 시 201(Created)과 생성된 memberId 반환.\n" +
			"\n**Request Parts:**\n" +
			"  - `storeId` (Long, required): 스토어 id\n" +
			"  - `year` (Integer, required): 년도\n" +
			"  - `month` (Long, required): 월\n"+
			"\n**Response:**\n" +
			"  - 성공 시 partnership Usage 내역 반환 \n"+
			"  - 해당 월에 사용한 제휴 수 반환"
	)
	public ResponseEntity<BaseResponse<StudentResponseDTO.myPartnership>> getMyPartnership(
		@PathVariable int year, @PathVariable int month, @AuthenticationPrincipal PrincipalDetails pd
	){
		StudentResponseDTO.myPartnership result = studentService.getMyPartnership(pd.getId(), year, month);

		return ResponseEntity.ok(BaseResponse.onSuccess(SuccessStatus.PARTNERSHIP_HISTORY_SUCCESS, result));
	}




	@Operation(
		summary = "사용자 stamp 개수 조회 API",
		description = "# [v1.0 (2025-09-09)](https://www.notion.so/_-2241197c19ed8134bd49d8841e841634?source=copy_link)\n" +
			"- `multipart/form-data`로 호출합니다.\n" +
			"- 처리: 정보 바탕으로 sessionManager에 session생성\n" +
			"\n**Response:**\n" +
			"  - stamp 개수 반환 \n"
	)
    @GetMapping("/stamp")
    public BaseResponse<StudentResponseDTO.CheckStampResponseDTO> getStamp(
            @AuthenticationPrincipal PrincipalDetails pd
    ) {
        return BaseResponse.onSuccess(SuccessStatus._OK, studentService.getStamp(pd.getId()));
    }
}
