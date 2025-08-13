package com.assu.server.domain.partner.dto;

import com.assu.server.domain.admin.entity.Admin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class PartnerResponseDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RandomAdminResponseDTO {
        private List<AdminLiteDTO> admins;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AdminLiteDTO {
        private Long adminId;
        private String adminAddress;
        private String adminDetailAddress;
        private String adminName;
    }
}
