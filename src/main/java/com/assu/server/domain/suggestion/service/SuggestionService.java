package com.assu.server.domain.suggestion.service;

import com.assu.server.domain.suggestion.dto.SuggestionRequestDTO;
import com.assu.server.domain.suggestion.dto.SuggestionResponseDTO;
import com.assu.server.domain.suggestion.entity.Suggestion;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

public interface SuggestionService {

    SuggestionResponseDTO.WriteSuggestionResponseDTO writeSuggestion(
            @RequestBody SuggestionRequestDTO.WriteSuggestionRequestDTO request
    );
}
