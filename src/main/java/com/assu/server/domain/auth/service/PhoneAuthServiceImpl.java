package com.assu.server.domain.auth.service;

import com.assu.server.global.apiPayload.code.status.ErrorStatus;
import com.assu.server.global.util.RandomNumberUtil;
import com.assu.server.domain.auth.exception.CustomAuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class PhoneAuthServiceImpl implements PhoneAuthService {

    private final StringRedisTemplate redisTemplate;

    private static final Duration AUTH_CODE_TTL = Duration.ofMinutes(5); // 인증번호 5분 유효

    @Async
    @Override
    public void sendAuthNumber(String phoneNumber) {
        String authNumber = RandomNumberUtil.generateSixDigit();

        ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
        valueOps.set(phoneNumber, authNumber, AUTH_CODE_TTL);

        // 알리고 API로 실제 문자 발송 처리 필요
        // 예: aligoService.sendSms(phoneNumber, authNumber);
        System.out.println("[SMS] 전송 대상: " + phoneNumber + ", 인증번호: " + authNumber);
    }

    @Override
    public void verifyAuthNumber(String phoneNumber, String authNumber) {
        ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
        String stored = valueOps.get(phoneNumber);

        if (stored == null || !stored.equals(authNumber)) {
            throw new CustomAuthException(ErrorStatus.NOT_VERIFIED_PHONE_NUMBER);
        }

        // 인증 성공 시 Redis에서 삭제(Optional)
        redisTemplate.delete(phoneNumber);
    }
}
