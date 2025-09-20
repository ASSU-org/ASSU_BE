package com.assu.server.domain.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class AdminResponseDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RandomPartnerResponseDTO {
        private Long partnerId;
        private String partnerAddress;
        private String partnerDetailAddress;
        private String partnerName;
        private String partnerUrl;
    }
}
