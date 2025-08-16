package com.assu.server.domain.auth.security;

import com.assu.server.domain.auth.exception.CustomAuthException;
import com.assu.server.global.apiPayload.code.status.ErrorStatus;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    @Value("${jwt.header}")
    private String jwtHeader;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;

    private static final AntPathMatcher PATH = new AntPathMatcher();
    private static final String[] WHITELIST = {
            "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html",
            "/swagger-resources/**", "/webjars/**",
            "/auth/**",           // ← 로그인/회원가입/인증 등은 토큰 없이 접근
            "/chat/**", "/suggestion/**", "/review/**",
            "/ws/**", "/pub/**", "/sub/**"
    };

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) return true; // CORS preflight 우회
        if (PATH.match("/auth/refresh", uri)) return false;               // 토큰 재발급은 필터 적용
        for (String p : WHITELIST) if (PATH.match(p, uri)) return true;   // 나머지 공개 경로 우회
        return false;                                                     // 보호 자원은 필터 적용
    }

    private static void checkAuthorizationHeader(String header) {
        log.info("-------------------#@@@@@------------------");
        if(header == null) {
            throw new CustomAuthException(ErrorStatus.JWT_TOKEN_NOT_RECEIVED);
        } else if (!header.startsWith("Bearer ")) {
            throw new CustomAuthException(ErrorStatus.JWT_TOKEN_OUT_OF_FORM);
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader(jwtHeader);

        // Refresh 전용 처리
        if (PATH.match("/auth/refresh", request.getRequestURI())) {
            final String refreshToken = request.getHeader("refreshToken");
            try {
                // 둘 다 필수
                checkAuthorizationHeader(authHeader);
                if (refreshToken == null) throw new CustomAuthException(ErrorStatus.JWT_TOKEN_NOT_RECEIVED);

                String accessToken = JwtUtil.getTokenFromHeader(authHeader);
                Claims claims = jwtUtil.validateTokenOnlySignature(accessToken); // 서명만 검증(만료 허용)
                Authentication authentication = jwtUtil.getAuthenticationFromExpiredAccessToken(accessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                jwtUtil.validateRefreshToken(refreshToken); // RT는 만료 허용 X
                chain.doFilter(request, response);
                return;
            } catch (Exception e) {
                // EntryPoint로 넘겨 통일 처리
                if (e instanceof CustomAuthException ce) {
                    request.setAttribute("exceptionCode", ce.getCode());
                    request.setAttribute("exceptionMessage", ce.getMessage());
                    request.setAttribute("exceptionHttpStatus", ce.getHttpStatus());
                }
                throw new InsufficientAuthenticationException(e.getMessage(), e);
            }
        }

        // 그 외(보호 자원): Authorization 헤더가 없으면 그냥 통과
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        try {
            String accessToken = JwtUtil.getTokenFromHeader(authHeader);
            jwtUtil.validateToken(accessToken);
            jwtUtil.isTokenBlacklisted(accessToken); // accessToken 전달

            Authentication authentication = jwtUtil.getAuthentication(accessToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            chain.doFilter(request, response);
        } catch (Exception e) {
            if (e instanceof CustomAuthException ce) {
                request.setAttribute("exceptionCode", ce.getCode());
                request.setAttribute("exceptionMessage", ce.getMessage());
                request.setAttribute("exceptionHttpStatus", ce.getHttpStatus());
            }
            throw new InsufficientAuthenticationException(e.getMessage(), e);
        }
    }
}

