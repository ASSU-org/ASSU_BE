package com.assu.server.domain.auth.security;

import com.assu.server.domain.auth.dto.signup.Tokens;
import com.assu.server.domain.auth.entity.Member;
import com.assu.server.domain.auth.exception.CustomAuthException;
import com.assu.server.domain.auth.repository.MemberRepository;
import com.assu.server.domain.common.enums.UserRole;
import com.assu.server.global.apiPayload.code.status.ErrorStatus;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.*;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    @Value("${jwt.secret}")
    public String secretKey;

    @Value("${jwt.access-valid-seconds:3600}")      // 1시간 기본
    private int accessValidSeconds;

    @Value("${jwt.refresh-valid-seconds:1209600}")  // 14일 기본
    private int refreshValidSeconds;

    private final RedisTemplate<String, String> redisTemplate;
    private final MemberRepository memberRepository;

    // 헤더에 "Bearer XXX" 형식으로 담겨온 토큰을 추출한다
    public static String getTokenFromHeader(String header) {
        return header.split(" ")[1];
    }

    public String generateToken(Map<String, Object> valueMap, int validTime) { // static 제거
        SecretKey key = null;
        try {
            key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        } catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }
        return Jwts.builder()
                .setHeader(Map.of("typ","JWT"))
                .setClaims(valueMap)
                .setIssuedAt(Date.from(ZonedDateTime.now().toInstant()))
                .setExpiration(Date.from(ZonedDateTime.now().plusSeconds(validTime).toInstant()))
                .signWith(key)
                .compact();
    }

    public Tokens issueAndPersistTokens(Member member, UserRole role) {
        // 공통 Claims
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", member.getId());
        claims.put("role", role.name());

        // access / refresh 발급
        String access = generateToken(claims, accessValidSeconds);
        String refresh = generateToken(claims, refreshValidSeconds);

        // 멤버 엔티티에 저장 (unique=true 컬럼)
        member.setAccessToken(access);
        member.setRefreshToken(refresh);
        memberRepository.save(member);

        return Tokens.builder()
                .accessToken(access)
                .refreshToken(refresh)
                .build();
    }

    public Authentication getAuthentication(String token) { // context에 넣을 Authentication를 jwt의 userId를 넣어 생성 // static 제거
        Map<String, Object> claims = validateToken(token);
        System.out.println("userId type: " + (claims.get("userId") != null ? claims.get("userId").getClass().getName() : "null"));

        Number uid = (Number) claims.get("userId");
        Long userId = uid.longValue();

        return new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
    }

    public Map<String, Object> validateToken(String token) { // static 제거
        Map<String, Object> claim = null;
        try {
            SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
            claim = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token) // 파싱 및 검증, 실패 시 에러
                    .getBody();
        } catch(ExpiredJwtException expiredJwtException){
            throw new CustomAuthException(ErrorStatus.JWT_ACCESS_TOKEN_EXPIRED);
        } catch(Exception e){
            throw new CustomAuthException(ErrorStatus.AUTHORIZATION_EXCEPTION);
        }
        return claim;
    }

    public Authentication getAuthenticationFromExpiredAccessToken(String token) { // context에 넣을 Authentication를 jwt의 userId를 넣어 생성 // static 제거
        Map<String, Object> claims = validateTokenOnlySignature(token);
        System.out.println("userId type: " + (claims.get("userId") != null ? claims.get("userId").getClass().getName() : "null"));

        Number uid = (Number) claims.get("userId");
        Long userId = uid.longValue();

        return new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
    }

    public Claims validateTokenOnlySignature(String token) { // static 제거
        Claims claims = null;
        try {
            SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
            claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token) // 파싱 및 검증, 실패 시 에러
                    .getBody();
        } catch(ExpiredJwtException expiredJwtException){
            return expiredJwtException.getClaims(); // ✅ 만료된 토큰에서도 Claims 추출
        } catch(Exception e){
            throw new CustomAuthException(ErrorStatus.AUTHORIZATION_EXCEPTION);
        }
        return claims;
    }

    public void validateRefreshToken(String token) { // static 제거
        Map<String, Object> claim = null;
        try {
            SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
            claim = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token) // 파싱 및 검증, 실패 시 에러
                    .getBody();
        } catch(ExpiredJwtException expiredJwtException){
            throw new CustomAuthException(ErrorStatus.JWT_REFRESH_TOKEN_EXPIRED);
        } catch(Exception e){
            throw new CustomAuthException(ErrorStatus.AUTHORIZATION_EXCEPTION);
        }
    }

    // 토큰의 남은 만료시간 계산
    public long tokenRemainTimeSecond(String header) { // static 제거
        String accessToken = getTokenFromHeader(header);
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(accessToken)
                .getBody();

        Date expDate = claims.getExpiration(); // 만료 시간 반환 (Date 타입)
        long remainMs = expDate.getTime() - System.currentTimeMillis();
        return remainMs / 1000;
    }

    // access token redis의 블랙리스트에서 확인
    public void isTokenBlacklisted(String accessToken) {
        Set<String> keys = redisTemplate.keys("blackList:*"); // "blackList:*" 패턴의 모든 Key 검색
        if (keys == null || keys.isEmpty()) {
            return; // 블랙리스트가 비어있다면 return
        }

        // 모든 Key에 대해 해당 Token이 Value로 존재하는지 확인
        for (String key : keys) {
            String value = redisTemplate.opsForValue().get(key);
            if (accessToken.equals(value)) {
                throw new CustomAuthException(ErrorStatus.LOGOUT_USER);
            }
        }
    }
}
