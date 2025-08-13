    package com.assu.server.domain.suggestion.dto;

    import com.assu.server.domain.admin.entity.Admin;
    import com.assu.server.domain.user.entity.enums.EnrollmentStatus;
    import com.assu.server.domain.user.entity.enums.Major;
    import lombok.AllArgsConstructor;
    import lombok.Builder;
    import lombok.Getter;
    import lombok.NoArgsConstructor;

    import java.time.LocalDateTime;

    public class SuggestionResponseDTO {

        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class WriteSuggestionResponseDTO {
            private Long suggestionId; // 제안 번호
            private Long memberId; // 제안인 아이디
            private Long studentNumber; // 제안인 학번
            private Long suggestionSubjectId; // 건의 대상 아이디
            private String suggestionStore; // 희망 가게 이름
            private String suggestionBenefit; // 희망 혜택
        }

        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class GetSuggestionResponseDTO {
            private Long suggestionId;
            private LocalDateTime createdAt;
            private String content;
            private Major studentMajor;
            private Long studentNumber;
            private EnrollmentStatus enrollmentStatus;
        }
    }
