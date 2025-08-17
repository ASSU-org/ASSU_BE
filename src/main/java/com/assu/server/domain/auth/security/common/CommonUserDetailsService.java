package com.assu.server.domain.auth.security.common;

import com.assu.server.domain.auth.entity.CommonAuth;
import com.assu.server.domain.auth.entity.Member;
import com.assu.server.domain.auth.exception.CustomAuthException;
import com.assu.server.domain.auth.repository.CommonAuthRepository;
import com.assu.server.domain.common.enums.ActivationStatus;
import com.assu.server.domain.common.enums.UserRole;
import com.assu.server.global.apiPayload.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommonUserDetailsService implements UserDetailsService {

    private final CommonAuthRepository commonAuthRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // CommonAuth: email/password 해시 저장 테이블
        CommonAuth commonAuth = commonAuthRepository.findByEmail(email)
                .orElseThrow(() -> new CustomAuthException(ErrorStatus.NO_SUCH_MEMBER));

        // 연관된 Member에서 역할/상태를 가져옴
        Member member = commonAuth.getMember();
        UserRole role = member.getRole();
        boolean enabled = member.getIsActivated().equals(ActivationStatus.ACTIVE); // ACTIVE면 true

        // 권한명은 스프링 시큐리티 규약에 따라 ROLE_ 접두를 붙임
        String authority = "ROLE_" + role.name();

        return org.springframework.security.core.userdetails.User
                .withUsername(commonAuth.getEmail())
                .password(commonAuth.getPassword()) // 반드시 BCrypt 등 해시
                .authorities(authority)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!enabled)
                .build();
    }

}

