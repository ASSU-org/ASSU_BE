package com.assu.server.domain.partnership.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.assu.server.domain.common.entity.Member;
import com.assu.server.domain.partnership.dto.PaperResponseDTO;
import com.assu.server.domain.partnership.service.PartnershipService;
import com.assu.server.global.apiPayload.BaseResponse;
import com.assu.server.global.apiPayload.code.status.SuccessStatus;
import com.assu.server.global.util.PrincipalDetails;
import com.assu.server.domain.partnership.dto.PartnershipRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@Tag(name = "제휴 요청 api", description = "최종적으로 @@ 제휴를 요청할때 사용하는 api ")
@RequiredArgsConstructor
public class PartnershipController {

	private final PartnershipService partnershipService;


	@PostMapping("/parntership/usage")
	@Operation(summary= "유저의 인증 후 최종적으로 호출", description = "인증완료 화면 전에 바로 호출되어 유저의 제휴 내역에 데이터가 들어가게 됩니다. (개인 인증인 경우도 포함됩니다.)")
	public ResponseEntity<BaseResponse<PaperResponseDTO.partnershipContent>> finalPartnershipRequest(
		@AuthenticationPrincipal PrincipalDetails userDetails,@RequestBody PartnershipRequestDTO.finalRequest dto
	) {
		Member member = userDetails.getMember();

		partnershipService.recordPartnershipUsage(dto, member);

		return ResponseEntity.ok(BaseResponse.onSuccessWithoutData(SuccessStatus.USER_PAPER_REQUEST_SUCCESS));
	}


}
