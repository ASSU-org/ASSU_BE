package com.assu.server.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/ws/**").permitAll() // websocket 경로 허용
                        .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf.disable()) // websocket은 csrf 필요 없음
                .formLogin(login -> login.disable())
                .httpBasic(basic  -> basic.disable());

        return http.build();
    }
}
