package com.assu.server.domain.auth.security.adapter;

import com.assu.server.domain.auth.crypto.StudentPasswordEncoder;
import com.assu.server.domain.auth.entity.AuthRealm;
import com.assu.server.domain.auth.entity.SSUAuth;
import com.assu.server.domain.auth.exception.CustomAuthException;
import com.assu.server.domain.auth.repository.SSUAuthRepository;
import com.assu.server.domain.common.enums.ActivationStatus;
import com.assu.server.domain.member.entity.Member;
import com.assu.server.global.apiPayload.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SSUAuthAdapter implements RealmAuthAdapter {
    private final SSUAuthRepository ssuAuthRepository;
    private final StudentPasswordEncoder studentPasswordEncoder; // AES-GCM 비교

    @Override public boolean supports(AuthRealm realm) { return realm == AuthRealm.SSU; }

    @Override
    public UserDetails loadUserDetails(String studentNumber) {
        SSUAuth sa = ssuAuthRepository.findByStudentNumber(studentNumber)
                .orElseThrow(() -> new CustomAuthException(ErrorStatus.NO_SUCH_MEMBER));
        var m = sa.getMember();
        boolean enabled = m.getIsActivated() == ActivationStatus.ACTIVE;
        String authority = "ROLE_" + m.getRole().name();

        return org.springframework.security.core.userdetails.User
                .withUsername(sa.getStudentNumber())
                .password(sa.getPasswordCipher()) // 암호문, 비교는 Encoder가 담당
                .authorities(authority)
                .accountExpired(false).accountLocked(false).credentialsExpired(false)
                .disabled(!enabled)
                .build();
    }

    @Override public Member loadMember(String studentNumber) {
        return ssuAuthRepository.findByStudentNumber(studentNumber)
                .orElseThrow(() -> new CustomAuthException(ErrorStatus.NO_SUCH_MEMBER))
                .getMember();
    }

    @Override
    public void registerCredentials(Member member, String studentNumber, String rawPassword) {
        if (ssuAuthRepository.existsByStudentNumber(studentNumber)) {
            throw new CustomAuthException(ErrorStatus.EXISTED_STUDENT);
        }
        String cipher = studentPasswordEncoder.encode(rawPassword);
        ssuAuthRepository.save(
                SSUAuth.builder()
                        .member(member)
                        .studentNumber(studentNumber)
                        .passwordCipher(cipher)
                        .isAuthenticated(true)
                        .build()
        );
    }

    @Override public PasswordEncoder passwordEncoder() { return studentPasswordEncoder; }
    @Override public String authRealmValue() { return "SSU"; }
}
