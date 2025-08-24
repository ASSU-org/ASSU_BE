package com.assu.server.global.config;

import com.assu.server.global.apiPayload.code.status.ErrorStatus;
import com.assu.server.global.exception.exception.GeneralException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;

import java.util.List;

@Component
@RequiredArgsConstructor
public class KakaoLocalClient {
    private final WebClient kakaoWebClient;

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
            private String x; // 경도 문자열
            private String y; // 위도 문자열
            private String place_url;
            private String distance; // 기준좌표 주면 미터 문자열
        }
        @Data public static class Meta { private Integer total_count; private Boolean is_end; }
    }

    public KakaoKeywordResp searchByKeyword(String query, Double x, Double y,
                                            Integer radius, Integer page, Integer size) {
        return kakaoWebClient.get()
                .uri(uri -> {
                    UriBuilder b = uri.path("/v2/local/search/keyword.json")
                            .queryParam("query", query)
                            .queryParam("page", page == null ? 1 : page)
                            .queryParam("size", size == null ? 15 : size); // 카카오 최대 15
                    if (x != null && y != null) {
                        b.queryParam("x", x).queryParam("y", y);
                        if (radius != null) b.queryParam("radius", radius);
                    }
                    return b.build();
                })
                .retrieve()
                .bodyToMono(KakaoKeywordResp.class)
                .block();
    }

    @Data
    public static class KakaoAddressResp {
        private List<Document> documents;
        @Data
        public static class Document {
            private String x;                 // 경도
            private String y;                 // 위도
            private RoadAddress road_address; // 있을 수도/없을 수도
        }
        @Data
        public static class RoadAddress {
            private String address_name;      // 도로명 전체
        }
    }

    @Data
    public static class Geo {
        private final Double lat;         // y (latitude)
        private final Double lng;         // x (longitude)
        private final String roadAddress; // nullable
    }

    public Geo geocodeByAddress(String query) {
        KakaoAddressResp resp = kakaoWebClient.get()
                .uri(u -> u.path("/v2/local/search/address.json").queryParam("query", query).build())
                .retrieve()
                .bodyToMono(KakaoAddressResp.class)
                .block();
        if (resp == null || resp.getDocuments() == null || resp.getDocuments().isEmpty())
            throw new GeneralException(ErrorStatus.NO_SUCH_ADDRESS);
        var d = resp.getDocuments().get(0);
        Double lng = Double.valueOf(d.getX());
        Double lat = Double.valueOf(d.getY());
        String road = d.getRoad_address() == null ? null : d.getRoad_address().getAddress_name();
        return new Geo(lat, lng, road);
    }
}

