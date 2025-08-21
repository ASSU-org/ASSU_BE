package com.assu.server.domain.map.dto;

import com.assu.server.domain.partnership.entity.enums.CriterionType;
import com.assu.server.domain.partnership.entity.enums.OptionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

public class MapResponseDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PartnerMapResponseDTO {
        private Long pinId;
        private Long partnerId;
        private String name;
        private String address;
        private boolean isPartnered;
        private Long partnershipId;
        private LocalDate partnershipStartDate;
        private LocalDate partnershipEndDate;
        private Double latitude;
        private Double longitude;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AdminMapResponseDTO {
        private Long pinId;
        private Long adminId;
        private String name;
        private String address;
        private boolean isPartnered;
        private Long partnershipId;
        private LocalDate partnershipStartDate;
        private LocalDate partnershipEndDate;
        private Double latitude;
        private Double longitude;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StoreMapResponseDTO {
        private Long pinId;
        private Long storeId;
        private Long adminId;
        private String name;
        private String address;
        private Integer rate;
        private CriterionType criterionType;
        private OptionType optionType;
        private Integer people;
        private Long cost;
        private String category;
        private Long discountRate;
        private boolean hasPartner;
        private Double latitude;
        private Double longitude;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SavePinResponseDTO {
        private Long pinId;
        private String ownerType;   // ADMIN / PARTNER / STORE
        private Long ownerId;
        private String name;
        private String address;
        private Double latitude;
        private Double longitude;
    }
}
