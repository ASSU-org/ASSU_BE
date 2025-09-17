    package com.assu.server.domain.suggestion.dto;

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
            private Long userId; // 제안인 아이디
            private Long adminId; // 건의 대상 아이디
            private String storeName; // 희망 가게 이름
            private String suggestionBenefit; // 희망 혜택
        }

        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class GetSuggestionResponseDTO {
            private Long suggestionId;
            private LocalDateTime createdAt;
            private String storeName;
            private String content;
            private Major studentMajor;
            private EnrollmentStatus enrollmentStatus;
        }

        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class GetSuggestionAdminsDTO {
            private Long adminId;
            private String adminName;
            private Long departId;
            private String departName;
            private Long majorId;
            private String majorName;
        }
    }
