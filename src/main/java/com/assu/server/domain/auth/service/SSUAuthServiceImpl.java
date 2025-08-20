package com.assu.server.domain.auth.service;


import com.assu.server.domain.user.entity.enums.Major;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SSUAuthServiceImpl implements SSUAuthService {
    /*
    @NotNull
    @Override
    public UsaintAuthReturnDto uSaintAuth(@NotNull UsaintAuthParamDto usaintAuthParamDto) throws APIRequestFailedException, AuthFailedException, HTMLParseFailedException, UnsupportedMajorException {
        String sToken = usaintAuthParamDto.getSToken();
        Integer sIdno = usaintAuthParamDto.getSIdno();

        // Phase 1 : uSaint SSO

        String uSaintSSORequestUrl = globalVariable.uSaintSSOUrl + "?sToken=" + sToken + "&sIdno=" + sIdno;

        HashMap<String, String> uSaintSSORequestHeaders = new HashMap<>();
        uSaintSSORequestHeaders.put("Cookie", "sToken=" + sToken + "; sIdno=" + sIdno);

        APIRequestDto uSaintSSORequestDto = APIRequestDto.builder()
                .headers(uSaintSSORequestHeaders)
                .url(uSaintSSORequestUrl)
                .build();

        APIResponseDto uSaintSSOResponseDto;
        try {
            uSaintSSOResponseDto = apiProvider.get(uSaintSSORequestDto);
        }
        catch (Exception e){
            log.error("API request to uSaint SSO failed.", e);
            throw new APIRequestFailedException();
        }

        if(!uSaintSSOResponseDto.getBody().contains("location.href = \"/irj/portal\";")){
            log.error("Student authentication with sToken {} and sIdno {} failed.", sToken, sIdno);
            throw new AuthFailedException();
        }

        Map<String, List<String>> uSaintSSOResponseHeaders = uSaintSSOResponseDto.getHeaders();
        List<String> setCookieList = uSaintSSOResponseHeaders.get("set-cookie");
        StringBuilder uSaintPortalCookie = new StringBuilder();

        for(String setCookie : setCookieList){
            setCookie = setCookie.split(";")[0];
            uSaintPortalCookie.append(setCookie).append("; ");
        }

        // Phase 2 : uSaint Portal

        String uSaintPortalRequestUrl = globalVariable.uSaintPortalUrl;

        HashMap<String, String> uSaintPortalRequestHeaders = new HashMap<>();
        uSaintPortalRequestHeaders.put("Cookie", uSaintPortalCookie.toString());

        APIRequestDto uSaintPortalRequestDto = APIRequestDto.builder()
                .headers(uSaintPortalRequestHeaders)
                .url(uSaintPortalRequestUrl)
                .build();

        APIResponseDto uSaintPortalResponseDto;
        try {
            uSaintPortalResponseDto = apiProvider.get(uSaintPortalRequestDto);
        }
        catch (Exception e){
            log.error("API request to uSaint Portal failed.", e);
            throw new APIRequestFailedException();
        }

        String uSaintPortalResponseBody = uSaintPortalResponseDto.getBody();
        UsaintAuthReturnDto usaintAuthReturnDto = UsaintAuthReturnDto.builder().build();

        Document uSaintPortalDocument = Jsoup.parse(uSaintPortalResponseBody);
        Element uSaintPortalNameBox = uSaintPortalDocument.getElementsByClass("main_box09").first();
        Element uSaintPortalInfoBox = uSaintPortalDocument.getElementsByClass("main_box09_con").first();
        if(uSaintPortalNameBox == null){
            log.error("uSaintPortalNameBox is null.");
            log.debug(uSaintPortalResponseBody);
            throw new HTMLParseFailedException();
        }
        if(uSaintPortalInfoBox == null){
            log.error("uSaintPortalInfoBox is null.");
            log.debug(uSaintPortalResponseBody);
            throw new HTMLParseFailedException();
        }

        Element uSaintPortalNameBoxSpan = uSaintPortalNameBox.getElementsByTag("span").first();
        if(uSaintPortalNameBoxSpan == null || uSaintPortalNameBoxSpan.text().equals("")){
            log.error("uSaintPortalNameBoxSpan is null or empty.");
            log.debug(uSaintPortalResponseBody);
            throw new HTMLParseFailedException();
        }
        String studentName = uSaintPortalNameBoxSpan.text();
        studentName = studentName.split("님")[0];
        usaintAuthReturnDto.setName(studentName);

        Elements uSaintPortalInfoBoxLis = uSaintPortalInfoBox.getElementsByTag("li");

        for(Element uSaintPortalInfoBoxLi : uSaintPortalInfoBoxLis){
            Element dt = uSaintPortalInfoBoxLi.getElementsByTag("dt").first();
            if(dt == null){
                log.error("dt in uSaintPortalInfoBoxLi is null.");
                log.debug(uSaintPortalResponseBody);
                throw new HTMLParseFailedException();
            }

            Element strong = uSaintPortalInfoBoxLi.getElementsByTag("strong").first();
            if(strong == null || strong.text().equals("")){
                log.error("strong in uSaintPortalInfoBoxLi is null or empty.");
                log.debug(uSaintPortalResponseBody);
                throw new HTMLParseFailedException();
            }

            if(dt.text().equals("학번")){
                try{
                    usaintAuthReturnDto.setId(Integer.valueOf(strong.text()));
                }
                catch(NumberFormatException e){
                    log.error("studentId in strong is not an integer.");
                    log.debug(uSaintPortalResponseBody);
                    throw new HTMLParseFailedException();
                }
            }
            else if(dt.text().equals("소속")){
                usaintAuthReturnDto.setMajor(strong.text());
            }
            else if(dt.text().equals("과정/학적")){
                usaintAuthReturnDto.setStatus(strong.text());
            }

        }

        if(usaintAuthReturnDto.getMajor().contains("전자정보공학부")){
            usaintAuthReturnDto.setMajor("infocom");
            return usaintAuthReturnDto;
        }

        switch (usaintAuthReturnDto.getMajor()) {
            case "컴퓨터학부" -> usaintAuthReturnDto.setMajor(Major.COM);
            case "소프트웨어학부" -> usaintAuthReturnDto.setMajor(Major.SW);
            case "글로벌미디어학부" -> usaintAuthReturnDto.setMajor(Major.GM);
            case "미디어경영학과" -> usaintAuthReturnDto.setMajor(Major.MB);
            case "AI융합학부" -> usaintAuthReturnDto.setMajor(Major.AI);
            case "전자정보공학부" -> usaintAuthReturnDto.setMajor(Major.EE);
            case "정보보호학과" -> usaintAuthReturnDto.setMajor(Major.IP);
            default -> {
                log.debug("{} is not a supported major.", usaintAuthReturnDto.getMajor());
                throw new UnsupportedMajorException();
            }
        }

        return usaintAuthReturnDto;
    }
    */

}
