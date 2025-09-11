package com.assu.server.domain.auth.service;


import com.assu.server.domain.auth.dto.ssu.USaintAuthRequest;
import com.assu.server.domain.auth.dto.ssu.USaintAuthResponse;
import com.assu.server.domain.auth.exception.CustomAuthException;
import com.assu.server.domain.user.entity.enums.Major;
import com.assu.server.global.apiPayload.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SSUAuthServiceImpl implements SSUAuthService {

    private final WebClient webClient;

    private static final String USaintSSOUrl = "https://saint.ssu.ac.kr/webSSO/sso.jsp";
    private static final String USaintPortalUrl = "https://saint.ssu.ac.kr/webSSUMain/main_student.jsp";

    @Override
    public USaintAuthResponse uSaintAuth(USaintAuthRequest uSaintAuthRequest) {

        String sToken = uSaintAuthRequest.getSToken();
        Integer sIdno = uSaintAuthRequest.getSIdno();

        // 1) SSO 로그인 요청
        ResponseEntity<String> uSaintSSOResponseEntity;
        try {
            uSaintSSOResponseEntity = requestUSaintSSO(sToken, sIdno);
        } catch (Exception e) {
            log.error("API request to uSaint SSO failed.", e);
            throw new CustomAuthException(ErrorStatus.SSU_SAINT_SSO_FAILED);
        }

        if (uSaintSSOResponseEntity == null || uSaintSSOResponseEntity.getBody() == null) {
            log.error("Empty response from USaint SSO. sToken={}, sIdno={}", sToken, sIdno);
            throw new CustomAuthException(ErrorStatus.SSU_SAINT_SSO_FAILED);
        }

        String body = uSaintSSOResponseEntity.getBody();
        if (!body.contains("location.href = \"/irj/portal\";")) {
            log.error("Invalid SSO response. sToken={}, sIdno={}", sToken, sIdno);
            throw new CustomAuthException(ErrorStatus.SSU_SAINT_SSO_FAILED);
        }

        // 쿠키 추출
        HttpHeaders headers = uSaintSSOResponseEntity.getHeaders();
        List<String> setCookieList = headers.get(HttpHeaders.SET_COOKIE);

        StringBuilder uSaintPortalCookie = new StringBuilder();
        if (setCookieList != null) {
            for (String setCookie : setCookieList) {
                setCookie = setCookie.split(";")[0];
                uSaintPortalCookie.append(setCookie).append("; ");
            }
        }

        // 2) 포털 접근
        ResponseEntity<String> portalResponse;
        try {
            portalResponse = requestUSaintPortal(uSaintPortalCookie);
        } catch (Exception e) {
            log.error("API request to uSaint Portal failed.", e);
            throw new CustomAuthException(ErrorStatus.SSU_SAINT_PORTAL_FAILED);
        }

        if (portalResponse == null || portalResponse.getBody() == null) {
            log.error("Empty response from uSaint Portal. cookie={}", uSaintPortalCookie);
            throw new CustomAuthException(ErrorStatus.SSU_SAINT_PORTAL_FAILED);
        }

        String uSaintPortalResponseBody = portalResponse.getBody();
        USaintAuthResponse usaintAuthResponse = USaintAuthResponse.builder().build();

        // 3) HTML 파싱
        Document doc;
        try {
            doc = Jsoup.parse(uSaintPortalResponseBody);
        } catch (Exception e) {
            log.error("Jsoup parsing failed.", e);
            throw new CustomAuthException(ErrorStatus.SSU_SAINT_PARSE_FAILED);
        }

        Element nameBox = doc.getElementsByClass("main_box09").first();
        Element infoBox = doc.getElementsByClass("main_box09_con").first();

        if (nameBox == null || infoBox == null) {
            log.error("Portal HTML structure parsing failed.");
            log.debug(uSaintPortalResponseBody);
            throw new CustomAuthException(ErrorStatus.SSU_SAINT_PARSE_FAILED);
        }

        // 이름 추출
        Element span = nameBox.getElementsByTag("span").first();
        if (span == null || span.text().isEmpty()) {
            log.error("Student name span not found or empty.");
            throw new CustomAuthException(ErrorStatus.SSU_SAINT_PARSE_FAILED);
        }
        usaintAuthResponse.setName(span.text().split("님")[0]);

        // 학번, 소속, 학적 상태, 학년학기 추출
        Elements infoLis = infoBox.getElementsByTag("li");
        for (Element li : infoLis) {
            Element dt = li.getElementsByTag("dt").first();
            Element strong = li.getElementsByTag("strong").first();

            if (dt == null || strong == null || strong.text().isEmpty()) {
                log.error("Missing dt/strong in infoBox. li={}", li);
                throw new CustomAuthException(ErrorStatus.SSU_SAINT_PARSE_FAILED);
            }

            switch (dt.text()) {
                case "학번" -> {
                    try {
                        usaintAuthResponse.setId(Integer.valueOf(strong.text()));
                    } catch (NumberFormatException e) {
                        log.error("Invalid studentId format: {}", strong.text());
                        throw new CustomAuthException(ErrorStatus.SSU_SAINT_PARSE_FAILED);
                    }
                }
                case "소속" -> {
                    // 원본 문자열 저장
                    String majorStr = strong.text();

                    // 매핑된 Enum 값 저장
                    switch (majorStr) {
                        case "컴퓨터학부" -> usaintAuthResponse.setMajor(Major.COM);
                        case "소프트웨어학부" -> usaintAuthResponse.setMajor(Major.SW);
                        case "글로벌미디어학부" -> usaintAuthResponse.setMajor(Major.GM);
                        case "미디어경영학과" -> usaintAuthResponse.setMajor(Major.MB);
                        case "AI융합학부" -> usaintAuthResponse.setMajor(Major.AI);
                        case "전자정보공학부" -> usaintAuthResponse.setMajor(Major.EE);
                        case "정보보호학과" -> usaintAuthResponse.setMajor(Major.IP);
                        default -> {
                            log.debug("{} is not a supported major.", majorStr);
                            throw new CustomAuthException(ErrorStatus.SSU_SAINT_UNSUPPORTED_MAJOR);
                        }
                    }
                }
                case "과정/학적" -> usaintAuthResponse.setEnrollmentStatus(strong.text());
                case "학년/학기" -> usaintAuthResponse.setYearSemester(strong.text());
            }
        }

        return usaintAuthResponse;
    }

    private ResponseEntity<String> requestUSaintSSO(String sToken, Integer sIdno) {
        String url = USaintSSOUrl + "?sToken=" + sToken + "&sIdno=" + sIdno;

        return webClient.get()
                .uri(url)
                .header("Cookie", "sToken=" + sToken + "; sIdno=" + sIdno)
                .retrieve()
                .toEntity(String.class)   // ResponseEntity<String> 전체 반환 (body + header 포함)
                .block();                 // 동기 방식
    }

    private ResponseEntity<String> requestUSaintPortal(StringBuilder cookie) {
        return webClient.get()
                .uri(USaintPortalUrl)
                .header(HttpHeaders.COOKIE, cookie.toString()) // StringBuilder → String 변환
                .retrieve()
                .toEntity(String.class)
                .block();
    }
}
