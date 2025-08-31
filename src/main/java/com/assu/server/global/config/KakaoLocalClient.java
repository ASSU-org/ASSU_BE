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

    /* ========= 공용 DTO ========= */
    @Data
    public static class KakaoKeywordResp {
        private List<Document> documents;
        private Meta meta;
        @Data public static class Document {
            private String id;
            private String place_name;
            private String category_name;
            private String category_group_code;
            private String category_group_name;
            private String phone;
            private String address_name;       // 지번
            private String road_address_name;  // 도로명
            private String x;                  // 경도
            private String y;                  // 위도
            private String place_url;
            private String distance;           // 좌표 바이어스/카테고리 검색시 제공 (문자열 m)
        }
        @Data public static class Meta {
            private Integer total_count;
            private Boolean is_end;
        }
    }

    @Data
    public static class KakaoAddressResp {
        private List<Document> documents;
        private Meta meta;
        @Data public static class Document {
            private Address address;
            private RoadAddress road_address;
            private String x;  // 일부 응답에는 상위에 x/y가 직접 들어오기도 함 (카카오 문서 참고)
            private String y;

            @Data public static class Address {
                private String address_name;
                private String x;
                private String y;
            }
            @Data public static class RoadAddress {
                private String address_name;
                private String x;
                private String y;
            }
        }
        @Data public static class Meta {
            private Integer total_count;
            private Boolean is_end;
        }
    }

    /* ========= 1) 키워드 검색 ========= */
    public KakaoKeywordResp searchByKeyword(String query, Double x, Double y,
                                            Integer radius, Integer page, Integer size) {
        return kakaoWebClient.get()
                .uri(uri -> {
                    UriBuilder b = uri.path("/v2/local/search/keyword.json")
                            .queryParam("query", query)
                            .queryParam("page", page == null ? 1 : page)
                            .queryParam("size", size == null ? 15 : size);
                    if (x != null && y != null) {
                        b.queryParam("x", x).queryParam("y", y);
                        if (radius != null) b.queryParam("radius", radius);
                    }
                    return b.build();
                })
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(KakaoKeywordResp.class)
                .block();
    }

    /* ========= 2) 주소 지오코딩 ========= */
    public KakaoAddressResp searchByAddress(String query, Integer page, Integer size) {
        return kakaoWebClient.get()
                .uri(uri -> uri.path("/v2/local/search/address.json")
                        .queryParam("query", query)
                        .queryParam("page", page == null ? 1 : page)
                        .queryParam("size", size == null ? 10 : size)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(KakaoAddressResp.class)
                .block();
    }

    /* ========= 3) 카테고리 근접 검색 ========= */
    public KakaoKeywordResp searchByCategory(String categoryGroupCode,
                                             Double x, Double y,
                                             Integer radius, Integer page, Integer size) {
        return kakaoWebClient.get()
                .uri(uri -> uri.path("/v2/local/search/category.json")
                        .queryParam("category_group_code", categoryGroupCode)
                        .queryParam("x", x)
                        .queryParam("y", y)
                        .queryParam("radius", radius == null ? 500 : radius) // m
                        .queryParam("page", page == null ? 1 : page)
                        .queryParam("size", size == null ? 15 : size)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(KakaoKeywordResp.class)
                .block();
    }
}
