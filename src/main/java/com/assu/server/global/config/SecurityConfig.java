package com.assu.server.global.config;

import com.assu.server.domain.auth.security.jwt.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {}) // 기본 CORS 구성 사용(필요하면 CorsConfigurationSource 빈 추가)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // ✅ WebSocket 핸드셰이크 허용 (네이티브 + SockJS 모두 포함)
                        .requestMatchers("/ws/**").permitAll()

                        // Swagger 등 공개 리소스
                        .requestMatchers(
                                "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html",
                                "/swagger-resources/**", "/webjars/**"
                        ).permitAll()

                        // 로그아웃은 인증 필요
                        .requestMatchers("/auth/logout").authenticated()

                        .requestMatchers(// Auth (로그아웃 제외)
                                "/auth/phone-verification/send",
                                "/auth/phone-verification/verify",
                                "/auth/students/signup",
                                "/auth/partners/signup",
                                "/auth/admins/signup",
                                "/auth/commons/login",
                                "/auth/students/login",
                                "/auth/students/ssu-verify"
                        ).permitAll()

                        // 나머지 요청은 JwtAuthFilter가 화이트리스트/보호자원 판별
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
        		.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }

}
