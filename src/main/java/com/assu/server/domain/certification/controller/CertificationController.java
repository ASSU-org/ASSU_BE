package com.assu.server.domain.certification.controller;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.assu.server.domain.certification.dto.CertificationRequestDTO;
import com.assu.server.domain.certification.dto.CertificationResponseDTO;
import com.assu.server.domain.certification.service.CertificationService;
import com.assu.server.domain.member.entity.Member;
import com.assu.server.domain.member.repository.MemberRepository;
import com.assu.server.global.apiPayload.BaseResponse;
import com.assu.server.global.apiPayload.code.status.ErrorStatus;
import com.assu.server.global.apiPayload.code.status.SuccessStatus;
import com.assu.server.global.exception.GeneralException;
import com.assu.server.global.util.PrincipalDetails;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@Tag(name = "제휴 인증 api", description = "qr인증과 관련된 api 입니다.")
@RequiredArgsConstructor
public class CertificationController {

	private final CertificationService certificationService;
	private final MemberRepository memberRepository; // 지금은 그냥 임시 데이터 하드 코딩이라 여기에 둔거여

	@PostMapping("/certification/session")
	@Operation(
		summary = "세션 정보 요청 API",
		description = "# [v1.0 (2025-09-09)](https://www.notion.so/22b1197c19ed80bb8484d99cc6ce715b?source=copy_link)\n" +
			"- `multipart/form-data`로 호출합니다.\n" +
			"- 파트: `payload`(JSON, CertificationRequest.groupRequest)\n" +
			"- 처리: 정보 바탕으로 sessionManager에 session생성\n" +
			"- 성공 시 201(Created)과 생성된 memberId 반환.\n" +
			"\n**Request Parts:**\n" +
			"  - `request` (JSON, required): `CertificationRequest.groupRequest` 객체\n" +
			"  - `people` (Integer, required): 인증이 필요한 인원\n" +
			"  - `storeId` (Long, required): 스토어 id\n"+
			"  - `adminId` (Long, required): 관리자 id\n"+
			"  - `tableNumber` (Integer, required): 테이블 넘버\n"+
			"\n**Response:**\n" +
			"  - 성공 시 201(Created)와 sessionId, adminId 반환"
	)
	public ResponseEntity<BaseResponse<CertificationResponseDTO.getSessionIdResponse>> getSessionId(
		@AuthenticationPrincipal PrincipalDetails pd,
		@RequestBody CertificationRequestDTO.groupRequest dto
	) {

		CertificationResponseDTO.getSessionIdResponse result = certificationService.getSessionId(dto, pd.getMember());

		return ResponseEntity.ok(BaseResponse.onSuccess(SuccessStatus.GROUP_SESSION_CREATE, result));
	}

	@MessageMapping("/certify")
	@Operation(summary = "그룹 세션 인증 api", description = "그룹에 대한 세션 인증 요청을 보냅니다.")
	public ResponseEntity<BaseResponse<Void>> certifyGroup(
		CertificationRequestDTO.groupSessionRequest dto  , PrincipalDetails pd

	) {
		certificationService.handleCertification(dto, pd.getMember());

		return ResponseEntity.ok(BaseResponse.onSuccess(SuccessStatus.GROUP_CERTIFICATION_SUCCESS, null));
	}

	@PostMapping("/certification/personal")
	@Operation(summary = "개인 인증 api", description = "사실 크게 필요없는데, 제휴 내역 통계를 위해 데이터를 post하는 api 입니다. "
		+ "가게 별 제휴를 조회하고 people값이 null 인 제휴를 선택한 경우 그룹 인증 대신 요청하는 api 입니다.")
	public ResponseEntity<BaseResponse<Void>> personalCertification(
		@AuthenticationPrincipal PrincipalDetails pd,
		@RequestBody CertificationRequestDTO.personalRequest dto
	) {
		certificationService.certificatePersonal(dto, pd.getMember());

		return ResponseEntity.ok(BaseResponse.onSuccessWithoutData(SuccessStatus.PERSONAL_CERTIFICATION_SUCCESS));
	}

}
