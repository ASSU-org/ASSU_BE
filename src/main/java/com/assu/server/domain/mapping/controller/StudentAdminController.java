package com.assu.server.domain.mapping.controller;

import com.assu.server.domain.mapping.dto.StudentAdminResponseDTO;
import com.assu.server.domain.mapping.service.StudentAdminService;
import com.assu.server.global.apiPayload.BaseResponse;
import com.assu.server.global.apiPayload.code.status.SuccessStatus;
import com.assu.server.global.util.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dashBoard")
public class StudentAdminController {
    private final StudentAdminService studentAdminService;
    @Operation(
            summary = "누적 가입자 수 조회 API",
            description = "admin으로 접근해주세요."
    )
    @GetMapping
    public BaseResponse<StudentAdminResponseDTO.CountAdminAuthResponseDTO> getCountAdmin(
            @AuthenticationPrincipal PrincipalDetails pd
            ) {
        Long memberId = pd.getMember().getId();
        return BaseResponse.onSuccess(SuccessStatus._OK, studentAdminService.getCountAdminAuth(memberId));
    }
    @Operation(
            summary = "신규 한 달 가입자 수 조회 API",
            description = "admin으로 접근해주세요."
    )
    @GetMapping("/new")
    public BaseResponse<StudentAdminResponseDTO.NewCountAdminResponseDTO> getNewStudentCountAdmin(
            @AuthenticationPrincipal PrincipalDetails pd
    ){
        Long memberId = pd.getMember().getId();
        return BaseResponse.onSuccess(SuccessStatus._OK, studentAdminService.getNewStudentCountAdmin(memberId));
    }

    @Operation(
            summary = "오늘 제휴 사용자 수 조회 API",
            description = "admin으로 접근해주세요."
    )
    @GetMapping("/countUser")
    public BaseResponse<StudentAdminResponseDTO.CountUsagePersonResponseDTO> getCountUser(
            @AuthenticationPrincipal PrincipalDetails pd
    ){
        Long memberId = pd.getMember().getId();
        return BaseResponse.onSuccess(SuccessStatus._OK, studentAdminService.getCountUsagePerson(memberId));
    }
    @Operation(
            summary = "제휴업체 누적별 1위 업체 조회 API",
            description = "adminId로 접근해주세요."
    )
        @GetMapping("/top")
        public BaseResponse<StudentAdminResponseDTO.CountUsageResponseDTO> getTopUsage(
                @AuthenticationPrincipal PrincipalDetails pd
    ) {
        Long memberId = pd.getMember().getId();
            return BaseResponse.onSuccess(SuccessStatus._OK, studentAdminService.getCountUsage(memberId));
        }

        /**
         * 제휴 업체별 누적 제휴 이용 현황 리스트 반환 (사용량 내림차순)
         */
        @Operation(
                summary = "제휴업체 누적 사용 수 내림차순 조회 API",
                description = "adminId로 접근해주세요."
        )
        @GetMapping("/usage")
        public BaseResponse<StudentAdminResponseDTO.CountUsageListResponseDTO> getUsageList(
                @AuthenticationPrincipal PrincipalDetails pd
        ) {
            Long memberId = pd.getMember().getId();
            return BaseResponse.onSuccess(SuccessStatus._OK, studentAdminService.getCountUsageList(memberId));
        }

}
