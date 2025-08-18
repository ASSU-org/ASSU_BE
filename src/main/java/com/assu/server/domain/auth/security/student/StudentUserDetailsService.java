package com.assu.server.domain.auth.security.student;

import com.assu.server.domain.member.entity.Member;
import com.assu.server.domain.auth.entity.SSUAuth;
import com.assu.server.domain.auth.repository.SSUAuthRepository;
import com.assu.server.domain.common.enums.ActivationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentUserDetailsService implements UserDetailsService {
    private final SSUAuthRepository ssuAuthRepository;

    @Override
    public UserDetails loadUserByUsername(String studentNumber) throws UsernameNotFoundException {
        SSUAuth ssuAuth = ssuAuthRepository.findByStudentNumber(studentNumber)
                .orElseThrow(() -> new UsernameNotFoundException(studentNumber));

        Member member = ssuAuth.getMember();

        String authority = "ROLE_" + member.getRole().name();
        boolean enabled = member.getIsActivated().equals(ActivationStatus.ACTIVE);

        // username = 학번, password = "암호문" (Decoder가 복호화/비교)
        return org.springframework.security.core.userdetails.User
                .withUsername(ssuAuth.getStudentNumber())
                .password(ssuAuth.getPasswordCipher()) // 평문 아님! cipher 그대로
                .authorities(authority)
                .disabled(!enabled)
                .accountLocked(false).accountExpired(false).credentialsExpired(false)
                .build();
    }
}

