package com.assu.server.global.config;

import com.assu.server.domain.auth.crypto.StudentPasswordEncoder;
import com.assu.server.domain.auth.security.*;
import com.assu.server.domain.auth.security.common.CommonUserDetailsService;
import com.assu.server.domain.auth.security.common.CommonUsernamePasswordAuthenticationToken;
import com.assu.server.domain.auth.security.student.StudentUserDetailsService;
import com.assu.server.domain.auth.security.student.StudentUsernamePasswordAuthenticationToken;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class AuthProviderConfig {

    private final CommonUserDetailsService commonUserDetailsService;
    private final StudentUserDetailsService studentUserDetailsService;
    private final PasswordEncoder passwordEncoder;                 // BCrypt (공통)
    private final StudentPasswordEncoder studentPasswordEncoder;   // 학생 전용(AES-GCM 복호화 비교)

    @Bean
    public DaoAuthenticationProvider commonAuthProvider() {
        DaoAuthenticationProvider p = new DaoAuthenticationProvider() {
            @Override public boolean supports(Class<?> authentication) {
                return CommonUsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
            }
        };
        p.setUserDetailsService(commonUserDetailsService);
        p.setPasswordEncoder(passwordEncoder);
        return p;
    }

    @Bean
    public DaoAuthenticationProvider studentAuthProvider() {
        DaoAuthenticationProvider p = new DaoAuthenticationProvider() {
            @Override public boolean supports(Class<?> authentication) {
                return StudentUsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
            }
        };
        p.setUserDetailsService(studentUserDetailsService);
        p.setPasswordEncoder(studentPasswordEncoder);
        return p;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            DaoAuthenticationProvider studentAuthProvider,
            DaoAuthenticationProvider commonAuthProvider
    ) {
        return new ProviderManager(List.of(studentAuthProvider, commonAuthProvider));
    }
}
