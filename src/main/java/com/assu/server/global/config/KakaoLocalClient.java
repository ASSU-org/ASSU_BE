package com.assu.server.global.config;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;

import java.util.List;

@Component
@RequiredArgsConstructor
public class KakaoLocalClient {
    private final WebClient kakaoWebClient;

    /* ---------- 기존 키워드 검색 ---------- */
    @Data
    public static class KakaoKeywordResp {
        private List<Document> documents;
        private Meta meta;
        @Data public static class Document {
            private String id;
            private String place_name;
            private String category_name;
            private String phone;
            private String address_name;
            private String road_address_name;
            private String x; // 경도
            private String y; // 위도
            private String place_url;
            private String distance; // 기준좌표 주면 미터 문자열
        }
        @Data public static class Meta { private Integer total_count; private Boolean is_end; }
    }

    public KakaoKeywordResp searchByKeyword(String query, Double x, Double y,
                                            Integer radius, Integer page, Integer size, String sort) {
        return kakaoWebClient.get()
                .uri(uri -> {
                    UriBuilder b = uri.path("/v2/local/search/keyword.json")
                            .queryParam("query", query)
                            .queryParam("page", page == null ? 1 : page)
                            .queryParam("size", size == null ? 15 : size);
                    if (x != null && y != null) {
                        b.queryParam("x", x).queryParam("y", y);
                        if (radius != null) b.queryParam("radius", radius);
                        if (sort != null) b.queryParam("sort", sort); // accuracy|distance
                    }
                    return b.build();
                })
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(KakaoKeywordResp.class)
                .block();
    }

    /* ---------- 주소 검색 ---------- */
    @Data
    public static class KakaoAddressResp {
        private List<Document> documents;
        private Meta meta;
        @Data public static class Document {
            // 도로명주소
            private RoadAddress road_address;
            // 지번주소
            private Address address;

            @Data public static class RoadAddress {
                private String address_name;
                private String region_1depth_name;
                private String region_2depth_name;
                private String region_3depth_name;
                private String road_name;
                private String underground_yn;
                private String main_building_no;
                private String sub_building_no;
                private String building_name;
                private String zone_no;
                private Double x; // 경도
                private Double y; // 위도
            }

            @Data public static class Address {
                private String address_name;
                private String region_1depth_name;
                private String region_2depth_name;
                private String region_3depth_name;
                private String mountain_yn;
                private String main_address_no;
                private String sub_address_no;
                private Double x; // 경도
                private Double y; // 위도
            }
        }
        @Data public static class Meta { private Integer total_count; }
    }

    public KakaoAddressResp searchAddress(String query, Integer page, Integer size) {
        return kakaoWebClient.get()
                .uri(uri -> uri.path("/v2/local/search/address.json")
                        .queryParam("query", query)
                        .queryParam("page", page == null ? 1 : page)
                        .queryParam("size", size == null ? 15 : size)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(KakaoAddressResp.class)
                .block();
    }
}

