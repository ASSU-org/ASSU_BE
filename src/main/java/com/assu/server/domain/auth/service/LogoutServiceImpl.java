package com.assu.server.domain.auth.service;

import com.assu.server.domain.member.repository.MemberRepository;
import com.assu.server.domain.auth.security.JwtUtil;
import com.assu.server.domain.auth.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class LogoutServiceImpl implements LogoutService {

    private MemberRepository memberRepository;

    private final JwtUtil jwtUtil;
    private final RedisTemplate<String,String> redisTemplate;

    @Override
    public void logout(String rawAccessToken) {
        Long userId = SecurityUtil.getCurrentUserId();

        // RT 무효화: DB에서 제거
        memberRepository.findById(userId).ifPresent(m -> {
            m.setRefreshToken(null);
            m.setAccessToken(null);
            memberRepository.save(m);
        });

        // Access 토큰 블랙리스트 등록
        long remainSec;
        try {
            // JwtUtil에 같은 로직의 오버로드를 추가해도 됨
            remainSec = jwtUtil.tokenRemainTimeSecond("Bearer " + rawAccessToken);
        } catch (Exception e) {
            remainSec = 0L;
        }
        if (remainSec > 0) {
            String key = "blackList:" + userId;
            redisTemplate.opsForValue().set(key, rawAccessToken, remainSec, TimeUnit.SECONDS);
        }
    }
}
