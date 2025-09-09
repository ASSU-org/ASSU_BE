package com.assu.server.domain.suggestion.controller;

import com.assu.server.domain.suggestion.dto.SuggestionRequestDTO;
import com.assu.server.domain.suggestion.dto.SuggestionResponseDTO;
import com.assu.server.domain.suggestion.service.SuggestionService;
import com.assu.server.global.apiPayload.BaseResponse;
import com.assu.server.global.apiPayload.code.status.SuccessStatus;
import com.assu.server.global.util.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Suggestion", description = "제휴 건의 API")
@RestController
@RequiredArgsConstructor // 파라미터가 있어야만 하는 생성자
@RequestMapping("/suggestion") // suggestion 아래에서 시작
public class SuggestionController {

    private final SuggestionService suggestionService;


    @PostMapping
    @Operation(
            summary = "제휴 건의 API",
            description = "[v1.0 (2025-09-03)](https://www.notion.so/_-2241197c19ed81e68840d565af59b534) 관리자에게 제휴를 건의합니다.\n"
    )
    public BaseResponse<SuggestionResponseDTO.WriteSuggestionResponseDTO> writeSuggestion(
            @RequestBody SuggestionRequestDTO.WriteSuggestionRequestDTO suggestionRequestDTO,
            @AuthenticationPrincipal PrincipalDetails pd
    ){
        Long userId = pd.getMember().getId();
        return BaseResponse.onSuccess(SuccessStatus._OK, suggestionService.writeSuggestion(suggestionRequestDTO, userId));
    }

    @GetMapping("/admin")
    @Operation(
            summary = "제휴 건의대상 조회 API",
            description = "[v1.0 (2025-09-03)](https://www.notion.so/_-2241197c19ed81e68840d565af59b534) 현재 로그인한 학생(User)이 제휴를 건의할 수 있는 학생회(Admin)을 조회합니다.\n"
    )
    public BaseResponse<SuggestionResponseDTO.GetSuggestionAdminsDTO> getSuggestionAdmins(
            @AuthenticationPrincipal PrincipalDetails pd
    ) {
        Long userId = pd.getMember().getId();
        return BaseResponse.onSuccess(SuccessStatus._OK, suggestionService.getSuggestionAdmins(userId));
    }

    @GetMapping("/list")
    @Operation(
            summary = "제휴 건의 조회 API",
            description = "모든 제휴 건의를 조회합니다."
    )
    public BaseResponse<List<SuggestionResponseDTO.GetSuggestionResponseDTO>> getSuggestions(
            @AuthenticationPrincipal PrincipalDetails pd
    ) {
        Long adminId = pd.getMember().getId();
        return BaseResponse.onSuccess(SuccessStatus._OK, suggestionService.getSuggestions(adminId));
    }
}
