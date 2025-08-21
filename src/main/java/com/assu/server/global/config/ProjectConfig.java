package com.assu.server.global.config;

import com.assu.server.domain.auth.crypto.AesGcmSchoolCredentialEncryptor;
import com.assu.server.domain.auth.crypto.SchoolCredentialEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Base64;

@Configuration
public class ProjectConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SchoolCredentialEncryptor schoolCredentialEncryptor(@Value("${assu.security.school-crypto.base64-key}") String base64key) {
        byte[] keyBytes = Base64.getDecoder().decode(base64key);
        int len = keyBytes.length; // 16, 24, 32만 허용
        if (len != 16 && len != 24 && len != 32) {
            throw new IllegalStateException("AES key must be 16/24/32 bytes after Base64 decoding");
        }
        return new AesGcmSchoolCredentialEncryptor(keyBytes);
    }
}
