package com.assu.server.domain.auth.security.adapter;

import com.assu.server.domain.auth.entity.AuthRealm;
import com.assu.server.domain.auth.entity.CommonAuth;
import com.assu.server.domain.auth.exception.CustomAuthException;
import com.assu.server.domain.auth.repository.CommonAuthRepository;
import com.assu.server.domain.common.enums.ActivationStatus;
import com.assu.server.domain.member.entity.Member;
import com.assu.server.global.apiPayload.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommonAuthAdapter implements RealmAuthAdapter {
    private final CommonAuthRepository commonAuthRepository;
    private final PasswordEncoder passwordEncoder; // BCrypt

    @Override
    public boolean supports(AuthRealm realm) {
        return realm == AuthRealm.COMMON;
    }

    @Override
    public UserDetails loadUserDetails(String email) {
        CommonAuth ca = commonAuthRepository.findByEmail(email)
                .orElseThrow(() -> new CustomAuthException(ErrorStatus.NO_SUCH_MEMBER));
        var m = ca.getMember();
        boolean enabled = m.getIsActivated() == ActivationStatus.ACTIVE;
        String authority = "ROLE_" + m.getRole().name();

        return org.springframework.security.core.userdetails.User
                .withUsername(ca.getEmail())
                .password(ca.getPassword()) // BCrypt 해시
                .authorities(authority)
                .accountExpired(false).accountLocked(false).credentialsExpired(false)
                .disabled(!enabled)
                .build();
    }

    @Override
    public Member loadMember(String email) {
        Member member = commonAuthRepository.findByEmail(email)
                .orElseThrow(() -> new CustomAuthException(ErrorStatus.NO_SUCH_MEMBER))
                .getMember();

        // 탈퇴된 회원이 다시 로그인하면 복구
        if (member.getDeletedAt() != null) {
            member.setDeletedAt(null);
            commonAuthRepository.save(member.getCommonAuth());
        }

        return member;
    }

    @Override
    public void registerCredentials(Member member, String email, String rawPassword) {
        if (commonAuthRepository.existsByEmail(email)) {
            throw new CustomAuthException(ErrorStatus.EXISTED_EMAIL);
        }
        String hash = passwordEncoder.encode(rawPassword);
        commonAuthRepository.save(
                CommonAuth.builder()
                        .member(member)
                        .email(email)
                        .password(hash)
                        .isEmailVerified(false)
                        .build());
    }

    @Override
    public PasswordEncoder passwordEncoder() {
        return passwordEncoder;
    }

    @Override
    public String authRealmValue() {
        return "COMMON";
    }
}
