package com.assu.server.domain.map.dto;

import lombok.*;

public class MapRequestDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ViewOnMapDTO {
        private double lng1;
        private double lat1;
        private double lng2;
        private double lat2;
        private double lng3;
        private double lat3;
        private double lng4;
        private double lat4;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ConfirmRequest {
        private String placeId;
        private String name;
        private String address;     // 지번
        private String roadAddress; // 도로명
        private Double longitude;   // x
        private Double latitude;    // y
    }
}
