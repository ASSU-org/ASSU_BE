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
}
