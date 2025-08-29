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
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Swagger 등 공개 리소스
                        .requestMatchers(
                                "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html",
                                "/swagger-resources/**", "/webjars/**"
                        ).permitAll()

                        // 로그인/회원가입/재발급만 공개
                        .requestMatchers(
                                "/auth/login/common",
                                "/auth/login/student",
                                "/auth/signup/**",
                                "/auth/refresh",
                                "/auth/phone-numbers/**"
                        ).permitAll()
						.requestMatchers(
								"/chat/**",
								"/suggestion/**",
								"/review/**",
								"/ws/**",
								"/pub/**",
								"/sub/**",
								"/auth/**",
								"/certification/**",
								"/store/**",
							"/proposal",
							"/partnership/**",
							"/user/**")
						.permitAll()

                        // 지도 API 공개
                        .requestMatchers("/map/**").permitAll()

                        // 로그아웃은 인증 필요
                        .requestMatchers("/auth/logout").authenticated()

                        // 나머지는 인증 필요
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable());
        return http.build();
    }

}
