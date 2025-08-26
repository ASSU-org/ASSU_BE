package com.assu.server.domain.map.dto;

import com.assu.server.domain.partnership.entity.enums.CriterionType;
import com.assu.server.domain.partnership.entity.enums.OptionType;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

public class MapResponseDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PartnerMapResponseDTO {
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

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class PlaceItem {
        private String placeId;         // Kakao place id (문자열)
        private String name;            // place_name
        private String category;        // category_name
        private String phone;           // phone
        private String address;         // address_name(지번)
        private String roadAddress;     // road_address_name(도로명)
        private Double longitude;       // x
        private Double latitude;        // y
        private String distance;        // 기준좌표 주면 미터(문자열)
        private String placeUrl;        // place_url
    }

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor @Builder
    public static class PlaceSearchResponse {
        private List<PlaceItem> items;
        private Integer totalCount;
        private Boolean isEnd;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ConfirmResponse {
        private Long ownerId;
        private String ownerType;   // ADMIN / PARTNER / STORE
        private String name;
        private String address;     // 저장된 대표 주소(도로명 우선)
        private Double longitude;
        private Double latitude;
    }
}
