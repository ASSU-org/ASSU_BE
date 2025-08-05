package com.assu.server.domain.suggestion.dto;

import com.assu.server.domain.admin.entity.Admin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class SuggestionResponseDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class WriteSuggestionResponseDTO {
        private Long suggestionId; // 제휴 번호
        private Long memberId; // 제안인 아이디
        private Long studentNumber; // 제안인 학번
        private Long suggestionSubjectId; // 건의 대상 아이디
        private String suggestionStore; // 희망 가게 이름
        private String suggestionBenefit; // 희망 혜택
    }
}
