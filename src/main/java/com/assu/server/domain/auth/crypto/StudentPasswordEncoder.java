package com.assu.server.domain.auth.crypto;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StudentPasswordEncoder implements PasswordEncoder {

    private final SchoolCredentialEncryptor encryptor;

    @Override
    public String encode(CharSequence rawPassword) {
        // 회원가입/갱신 시 암호문 저장이 필요하면 사용 (AES-GCM 암호화)
        return encryptor.encrypt(rawPassword.toString());
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedCipher) {
        try {
            String plain = encryptor.decrypt(encodedCipher);
            return constantTimeEquals(plain, rawPassword.toString());
        } catch (Exception e) {
            return false;
        }
    }

    private boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null || a.length() != b.length()) return false;
        int r = 0;
        for (int i = 0; i < a.length(); i++) r |= a.charAt(i) ^ b.charAt(i);
        return r == 0;
    }
}
