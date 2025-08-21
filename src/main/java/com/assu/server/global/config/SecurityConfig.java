package com.assu.server.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class    SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 비활성화
                .csrf(csrf -> csrf.disable())
                // CORS 기본값(필요 없으면 이 줄 삭제해도 됨)
                .cors(Customizer.withDefaults())
                // 모든 요청 허용
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                // 세션 미사용 (원하면 STATELESS 유지, 필요 없으면 이 줄 삭제 가능)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 폼 로그인/HTTP Basic 비활성화
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable());

        return http.build();
    }
}
