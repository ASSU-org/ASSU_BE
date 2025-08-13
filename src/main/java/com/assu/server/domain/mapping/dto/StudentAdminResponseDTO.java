package com.assu.server.domain.mapping.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class StudentAdminResponseDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CountAdminAuthResponseDTO{ // admin에 따른 총 누적 가입자 수
        private Long studentCount;
        private Long adminId;
        private String adminName;
    }
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class NewCountAdminResponseDTO{ //신규 가입자수 (매달 1일 초기화)
        private Long newStudentCount;
        private Long adminId;
        private String adminName;
    }
}
