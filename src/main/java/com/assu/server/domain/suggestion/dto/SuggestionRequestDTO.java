package com.assu.server.domain.suggestion.dto;

import lombok.Getter;

public class SuggestionRequestDTO {

    @Getter
    public static class WriteSuggestionRequestDTO{
        private Long adminId; // 건의 대상
        private String storeName; // 희망 가게
        private String benefit; // 희망 혜택
    }
}
