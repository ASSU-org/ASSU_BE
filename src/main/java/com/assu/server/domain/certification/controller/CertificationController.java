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
import com.assu.server.domain.common.entity.Member;
import com.assu.server.domain.common.enums.ActivationStatus;
import com.assu.server.domain.common.enums.UserRole;
import com.assu.server.domain.user.entity.Student;
import com.assu.server.domain.user.entity.enums.EnrollmentStatus;
import com.assu.server.domain.user.entity.enums.Major;
import com.assu.server.domain.user.repository.StudentRepository;
import com.assu.server.global.apiPayload.BaseResponse;
import com.assu.server.global.apiPayload.code.status.SuccessStatus;
import com.assu.server.global.util.PrincipalDetails;
import com.fasterxml.jackson.databind.ser.Serializers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@Tag(name = "제휴 인증 api", description = "qr인증과 관련된 api 입니다.")
@RequiredArgsConstructor
public class CertificationController {

	private final CertificationService certificationService;

	@PostMapping("/certification/session")
	@Operation(summary = "세션 정보를 요청하는 api", description = "인원 수 기준이 요구되는 제휴일 때 세션을 만들고, 대표자 QR에 담을 정보를 요청하는 api 입니다.")
	public ResponseEntity<BaseResponse<CertificationResponseDTO.getSessionIdResponse>> getSessionId(
		@AuthenticationPrincipal PrincipalDetails userDetails,
		@RequestBody CertificationRequestDTO.groupRequest dto
	) {

		Member member = userDetails.getMember();
		CertificationResponseDTO.getSessionIdResponse result = certificationService.getSessionId(dto, member);

		return ResponseEntity.ok(BaseResponse.onSuccess(SuccessStatus.GROUP_SESSION_CREATE, result));
	}

	@MessageMapping("/certify")
	@Operation(summary = "그룹 세션 인증 api", description = "그룹에 대한 세션 인증 요청을 보냅니다.")
	public ResponseEntity<BaseResponse> certifyGroup(
		CertificationRequestDTO.groupSessionRequest dto  // 나중에 여기에 Security + WebSocket 설정 완료한 후
		// @AuthenticationPrincipal 넣어주기

	) {
		// 일단 더미 유저로
		Member member = new Member();
		member.setId(1L);
		member.setIsActivated(ActivationStatus.ACTIVE);
		member.setRole(UserRole.USER);
		member.setIsPhoneVerified(true);
		member.setPhoneNum("01012345678");
		member.setPhoneVerifiedAt(LocalDateTime.now());

		Student dummyStudent = Student.builder()
			.member(member)
			.department("IT대학")
			.enrollmentStatus(EnrollmentStatus.ENROLLED)
			.yearSemester("2025-1")
			.university("숭실대학교")
			.stamp(0)
			.major(Major.COM)
			.build();

		// Member와 StudentProfile 연결 (양방향인 경우)
		member.setStudentProfile(dummyStudent);

		certificationService.handleCertification(dto, member);

		return ResponseEntity.ok(BaseResponse.onSuccess(SuccessStatus.GROUP_CERTIFICATION_SUCCESS, null));
	}

	@PostMapping("/certification/personal")
	@Operation(summary = "개인 인증 api", description = "사실 크게 필요없는데, 제휴 내역 통계를 위해 데이터를 post하는 api 입니다. "
		+ "가게 별 제휴를 조회하고 people값이 null 인 제휴를 선택한 경우 그룹 인증 대신 요청하는 api 입니다.")
	public ResponseEntity<BaseResponse<Void>> personalCertification(
		@AuthenticationPrincipal PrincipalDetails userDetails,
		@RequestBody CertificationRequestDTO.personalRequest dto
	) {

		Member member = userDetails.getMember();
		certificationService.certificatePersonal(dto, member);

		return ResponseEntity.ok(BaseResponse.onSuccessWithoutData(SuccessStatus.PERSONAL_CERTIFICATION_SUCCESS));
	}

}
