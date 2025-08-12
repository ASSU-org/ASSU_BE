package com.assu.server.domain.user.controller;

import com.assu.server.domain.user.dto.StudentResponseDTO;
import com.assu.server.domain.user.service.StudentService;
import com.assu.server.global.apiPayload.BaseResponse;
import com.assu.server.global.apiPayload.code.status.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class StudentController {
    private final StudentService studentService;

    @Operation(
            summary = "스탬프 조회",
            description = "Authorization 후에 사용해주세요."
    )
    @GetMapping("/stamp")
    public BaseResponse<StudentResponseDTO.CheckStampResponseDTO> getStamp() {
        return BaseResponse.onSuccess(SuccessStatus._OK, studentService.getStamp());
    }
}