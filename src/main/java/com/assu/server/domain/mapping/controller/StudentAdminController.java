package com.assu.server.domain.mapping.controller;

import com.assu.server.domain.mapping.dto.StudentAdminResponseDTO;
import com.assu.server.domain.mapping.service.StudentAdminService;
import com.assu.server.global.apiPayload.BaseResponse;
import com.assu.server.global.apiPayload.code.status.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class StudentAdminController {
    private final StudentAdminService studentAdminService;
    @Operation(
            summary = "누적 가입수 조회 API입니다.",
            description = "admin으로 접근해주세요."
    )
    @GetMapping
    public BaseResponse<StudentAdminResponseDTO.CountAdminAuthResponseDTO> getCountAdmin() {
        return BaseResponse.onSuccess(SuccessStatus._OK, studentAdminService.getCountAdminAuth());
    }

}
