package com.assu.server.domain.partnership.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.assu.server.domain.member.entity.Member;
import com.assu.server.domain.partnership.dto.PaperResponseDTO;
import com.assu.server.domain.partnership.dto.PartnershipRequestDTO;
import com.assu.server.domain.partnership.dto.PartnershipResponseDTO;
import com.assu.server.domain.partnership.service.PartnershipService;
import com.assu.server.global.apiPayload.BaseResponse;
import com.assu.server.global.apiPayload.code.status.SuccessStatus;
import com.assu.server.global.util.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
@RestController
@Tag(name = "제휴 요청 api", description = "최종적으로 @@ 제휴를 요청할때 사용하는 api ")
@RequiredArgsConstructor
@RequestMapping("/partnership")
public class PartnershipController {

	private final PartnershipService partnershipService;


	@PostMapping("/usage")
	@Operation(summary= "유저의 인증 후 최종적으로 호출", description = "인증완료 화면 전에 바로 호출되어 유저의 제휴 내역에 데이터가 들어가게 됩니다. (개인 인증인 경우도 포함됩니다.)")
	public ResponseEntity<BaseResponse<PaperResponseDTO.partnershipContent>> finalPartnershipRequest(
		@AuthenticationPrincipal PrincipalDetails userDetails,@RequestBody PartnershipRequestDTO.finalRequest dto
	) {
		Member member = userDetails.getMember();

		partnershipService.recordPartnershipUsage(dto, member);

		return ResponseEntity.ok(BaseResponse.onSuccessWithoutData(SuccessStatus.USER_PAPER_REQUEST_SUCCESS));
	}

    @PatchMapping("/proposal")
    @Operation(
            summary = "제휴 제안서 내용 수정 API",
            description = "제공 서비스 종류(SERVICE, DISCOUNT), 서비스 제공 기준(PRICE, HEADCOUNT), 서비스 제공 항목, 카테고리, 할인율을 상황에 맞게 작성해주세요."
    )
    public BaseResponse<PartnershipResponseDTO.WritePartnershipResponseDTO> updatePartnership(
            @RequestBody PartnershipRequestDTO.WritePartnershipRequestDTO request,
            @AuthenticationPrincipal PrincipalDetails pd
    ){
        return BaseResponse.onSuccess(SuccessStatus._OK, partnershipService.updatePartnership(request, pd.getId()));
    }

    @Operation(
            summary = "제휴 제안서 수동 등록 API",
            description = "제공 서비스 종류(SERVICE, DISCOUNT), 서비스 제공 기준(PRICE, HEADCOUNT), 서비스 제공 항목, 카테고리, 할인율을 상황에 맞게 작성하고, 계약서 이미지를 업로드하세요."
    )
    @PostMapping(value = "/passivity", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse<PartnershipResponseDTO.ManualPartnershipResponseDTO> createManualPartnership(
            @RequestPart("request") @Parameter PartnershipRequestDTO.ManualPartnershipRequestDTO request,
            @Parameter(
                    description = "계약서 이미지 파일",
                    required = true,
                    content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE,
                            schema = @Schema(type = "string", format = "binary"))
            )
            MultipartFile contractImage,
            @AuthenticationPrincipal PrincipalDetails pd
    ) {
        return BaseResponse.onSuccess(SuccessStatus._OK, partnershipService.createManualPartnership(request, pd.getId(), contractImage));
    }

    @Operation(
            summary = "제휴 중인 가게 조회 API",
            description = "전체를 조회하려면 all을 true로, 가장 최근 두 건을 조회하려면 all을 false로 설정해주세요."
    )
    @GetMapping("/admin")
    public BaseResponse<List<PartnershipResponseDTO.WritePartnershipResponseDTO>> listForAdmin(
            @RequestParam(name = "all", defaultValue = "false") boolean all,
            @AuthenticationPrincipal PrincipalDetails pd
    ) {
        return BaseResponse.onSuccess(SuccessStatus._OK, partnershipService.listPartnershipsForAdmin(all, pd.getId()));
    }

    @Operation(
            summary = "제휴 중인 관리자 조회 API",
            description = "전체를 조회하려면 all을 true로, 가장 최근 두 건을 조회하려면 all을 false로 설정해주세요."
    )
    @GetMapping("/partner")
    public BaseResponse<List<PartnershipResponseDTO.WritePartnershipResponseDTO>> listForPartner(
            @RequestParam(name = "all", defaultValue = "false") boolean all,
            @AuthenticationPrincipal PrincipalDetails pd
    ) {
        return BaseResponse.onSuccess(SuccessStatus._OK, partnershipService.listPartnershipsForPartner(all, pd.getId()));
    }

    @Operation(
            summary = "제휴 상세조회 API",
            description = "제휴 아이디를 입력하세요."
    )
    @GetMapping("/{partnershipId}")
    public BaseResponse<PartnershipResponseDTO.WritePartnershipResponseDTO> getPartnership(
            @PathVariable Long partnershipId
    ) {
        return BaseResponse.onSuccess(SuccessStatus._OK, partnershipService.getPartnership(partnershipId));
    }

    @Operation(
            summary = "제휴 상태 업데이트 API",
            description = "제휴 ID와 바꾸고 싶은 상태를 입력하세요(SUSPEND/ACTIVE/INACTIVE)"
    )
    @PatchMapping("/{partnershipId}/status")
    public BaseResponse<PartnershipResponseDTO.UpdateResponseDTO> updatePartnershipStatus(
            @PathVariable("partnershipId") Long partnershipId,
            @RequestBody PartnershipRequestDTO.UpdateRequestDTO request
    ) {
        return BaseResponse.onSuccess(SuccessStatus._OK, partnershipService.updatePartnershipStatus(partnershipId, request));
    }

    @PostMapping("/proposal/draft")
    @Operation(
            summary = "제휴 제안서 초안 생성 API",
            description = "현재 로그인한 관리자(Admin)가 내용이 비어있는 제휴 제안서를 초안 상태로 생성합니다."
    )
    public BaseResponse<PartnershipResponseDTO.CreateDraftResponseDTO> createDraftPartnership(
            @RequestBody PartnershipRequestDTO.CreateDraftRequestDTO request,
            @AuthenticationPrincipal PrincipalDetails pd
    ) {
        return BaseResponse.onSuccess(SuccessStatus._OK, partnershipService.createDraftPartnership(request, pd.getId()));
    }

    @DeleteMapping("/proposal/delete/{paperId}")
    @Operation(
            summary = "제휴 제안서 삭제 API",
            description = "특정 제휴 제안서(paperId)와 관련된 모든 데이터를 삭제합니다."
    )
    public BaseResponse<Void> deletePartnership(
            @PathVariable Long paperId
    ) {
        partnershipService.deletePartnership(paperId);
        return BaseResponse.onSuccess(SuccessStatus._OK, null);
    }

    @GetMapping("/suspended")
    @Operation(
            summary = "대기 중인 제휴 계약서 조회 API",
            description = "현재 로그인한 관리자(Admin)가 대기 중인 제휴 계약서를 모두 조회하여 리스트로 반환합니다."
    )
    public BaseResponse<List<PartnershipResponseDTO.SuspendedPaperDTO>> suspendPartnership(
            @AuthenticationPrincipal PrincipalDetails pd
    ) {
        return BaseResponse.onSuccess(SuccessStatus._OK, partnershipService.getSuspendedPapers(pd.getId()));
    }

    @GetMapping("/check/admin")
    @Operation(
            summary = "관리자 채팅방 내 제휴 확인 API",
            description = "현재 로그인한 관리자(Admin)가 파라미터로 받은 partnerId를 가진 상대 제휴업체(Partner)와 맺고 있는 제휴를 조회합니다. 비활성화되지 않은 가장 최근 제휴 1건을 조회합니다."
    )
    public BaseResponse<PartnershipResponseDTO.AdminPartnershipWithPartnerResponseDTO> checkAdminPartnership(
            @RequestParam("partnerId") Long partnerId,
            @AuthenticationPrincipal PrincipalDetails pd
    ) {
        return BaseResponse.onSuccess(SuccessStatus._OK, partnershipService.checkPartnershipWithPartner(pd.getId(), partnerId));
    }

    @GetMapping("/check/partner")
    @Operation(
            summary = "제휴업체 채팅방 내 제휴 확인 API",
            description = "현재 로그인한 제휴업체(Partner)가 파라미터로 받은 AdminId를 가진 상대 관리자(Admin)과 맺고 있는 제휴를 조회합니다. 비활성화되지 않은 가장 최근 제휴 1건을 조회합니다."
    )
    public BaseResponse<PartnershipResponseDTO.PartnerPartnershipWithAdminResponseDTO> checkPartnerPartnership(
            @RequestParam("adminId") Long adminId,
            @AuthenticationPrincipal PrincipalDetails pd
    ) {
        return BaseResponse.onSuccess(SuccessStatus._OK, partnershipService.checkPartnershipWithAdmin(pd.getId(), adminId));
    }
}
