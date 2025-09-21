package com.assu.server.domain.suggestion.service;

import com.assu.server.domain.suggestion.dto.SuggestionRequestDTO;
import com.assu.server.domain.suggestion.dto.SuggestionResponseDTO;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface SuggestionService {

    SuggestionResponseDTO.WriteSuggestionResponseDTO writeSuggestion(
            @RequestBody SuggestionRequestDTO.WriteSuggestionRequestDTO request,
            Long userId
    );

    List<SuggestionResponseDTO.GetSuggestionResponseDTO> getSuggestions(Long adminId);

    SuggestionResponseDTO.GetSuggestionAdminsDTO getSuggestionAdmins(Long userId);
}
