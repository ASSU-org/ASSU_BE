package com.assu.server.infra.aligo.client;

import com.assu.server.domain.auth.exception.CustomAuthException;
import com.assu.server.global.apiPayload.code.status.ErrorStatus;
import com.assu.server.infra.aligo.dto.AligoSendResponse;
import com.assu.server.infra.aligo.exception.AligoException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class AligoSmsClient {

    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${aligo.key}")
    private String apiKey;

    @Value("${aligo.user-id}")
    private String userId;

    @Value("${aligo.sender}")
    private String sender;

    private static final String SEND_URL = "https://apis.aligo.in/send/";

    public AligoSendResponse sendSms(String phoneNumber, String message, String name) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("key", apiKey);
        params.add("userid", userId);
        params.add("sender", sender);
        params.add("receiver", phoneNumber);
        params.add("msg", message);
        params.add("msg_type", "SMS");
        params.add("destination", phoneNumber + "|" + name);

        String body = webClient.post()
                .uri(SEND_URL)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(params))
                .retrieve()
                .bodyToMono(String.class)
                .block(); // 동기로 변환

        try {
            return objectMapper.readValue(body, AligoSendResponse.class);
        } catch (Exception e) {
            throw new AligoException(ErrorStatus.FAILED_TO_SEND_SMS);
        }
    }
}

