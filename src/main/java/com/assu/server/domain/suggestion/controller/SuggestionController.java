package com.assu.server.domain.suggestion.controller;

import com.assu.server.domain.suggestion.dto.SuggestionRequestDTO;
import com.assu.server.domain.suggestion.dto.SuggestionResponseDTO;
import com.assu.server.domain.suggestion.service.SuggestionService;
import com.assu.server.global.apiPayload.BaseResponse;
import com.assu.server.global.apiPayload.code.status.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor // 파라미터가 있어야만 하는 생성자
@RequestMapping("/suggestion") // suggestion 아래에서 시작
public class SuggestionController {

    private final SuggestionService suggestionService;

    @Operation(
            summary = "제휴 건의를 하는 API 입니다.",
            description = "건의대상, 제휴 희망 가게, 희망 혜택을 입력해주세요."
    )
    @PostMapping()
    public BaseResponse<SuggestionResponseDTO.WriteSuggestionResponseDTO> writeSuggestion(
            @RequestBody SuggestionRequestDTO.WriteSuggestionRequestDTO suggestionRequestDTO
    ){
        return BaseResponse.onSuccess(SuccessStatus._OK, suggestionService.writeSuggestion(suggestionRequestDTO));
    }
}
