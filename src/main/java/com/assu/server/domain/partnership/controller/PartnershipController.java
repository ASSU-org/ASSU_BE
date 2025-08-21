package com.assu.server.domain.partnership.controller;

import com.assu.server.domain.partnership.dto.PartnershipRequestDTO;
import com.assu.server.domain.partnership.dto.PartnershipResponseDTO;
import com.assu.server.domain.partnership.service.PartnershipService;
import com.assu.server.global.apiPayload.BaseResponse;
import com.assu.server.global.apiPayload.code.status.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/partnership")
public class PartnershipController {

    private final PartnershipService partnershipService;

    @Operation(
            summary = "제휴 제안서를 작성하는 API 입니다.",
            description = "제공 서비스 종류(서비스 제공, 할인), 서비스 제공 기준(금액, 인원수), 서비스 제공 항목, 카테고리, 할인율을 상황에 맞게 작성해주세요."
    )
    @PostMapping("/proposal")
    public BaseResponse<PartnershipResponseDTO.WritePartnershipResponseDTO> writePartnership(
            @RequestBody PartnershipRequestDTO.WritePartnershipRequestDTO partnershipRequestDTO
    ){
        return BaseResponse.onSuccess(SuccessStatus._OK, partnershipService.writePartnership(partnershipRequestDTO));
    }

    @Operation(
            summary = "제휴를 조회하는 API 입니다.",
            description = "전체를 조회하려면 all을 true로, 가장 최근 두 건을 조회하려면 all을 false로 설정해주세요."
    )
    @GetMapping
    public BaseResponse<List<PartnershipResponseDTO.WritePartnershipResponseDTO>> list(
            @RequestParam(name = "all", defaultValue = "false") boolean all
    ) {
        return BaseResponse.onSuccess(SuccessStatus._OK, partnershipService.listPartnerships(all));
    }

    @Operation(
            summary = "제휴를 상세조회하는 API 입니다.",
            description = "제휴 아이디를 입력하세요."
    )
    @GetMapping("/{partnershipId}")
    public BaseResponse<PartnershipResponseDTO.WritePartnershipResponseDTO> getPartnership(
            @PathVariable Long partnershipId
    ) {
        return BaseResponse.onSuccess(SuccessStatus._OK, partnershipService.getPartnership(partnershipId));
    }

    @Operation(
            summary = "제휴 상태를 업데이트하는 API 입니다.",
            description = "바꾸고 싶은 상태를 입력하세요(SUSPEND/ACTIVE/INACTIVE)"
    )
    @PatchMapping("/{partnershipId}/status")
    public BaseResponse<PartnershipResponseDTO.UpdateResponseDTO> updatePartnershipStatus(
            @PathVariable("partnershipId") Long partnershipId,
            @RequestBody PartnershipRequestDTO.UpdateRequestDTO request
    ) {
        return BaseResponse.onSuccess(SuccessStatus._OK, partnershipService.updatePartnershipStatus(partnershipId, request));
    }

}
