package com.assu.server.domain.auth.service;

import com.assu.server.domain.auth.dto.login.LoginRequest;
import com.assu.server.domain.auth.dto.login.LoginResponse;
import com.assu.server.domain.auth.dto.login.RefreshResponse;
import com.assu.server.domain.auth.dto.login.StudentLoginRequest;
import com.assu.server.domain.auth.dto.signup.Tokens;
import com.assu.server.domain.auth.entity.CommonAuth;
import com.assu.server.domain.member.entity.Member;
import com.assu.server.domain.auth.entity.SSUAuth;
import com.assu.server.domain.auth.exception.CustomAuthException;
import com.assu.server.domain.auth.repository.CommonAuthRepository;
import com.assu.server.domain.member.repository.MemberRepository;
import com.assu.server.domain.auth.repository.SSUAuthRepository;
import com.assu.server.domain.auth.security.JwtUtil;
import com.assu.server.domain.auth.security.SecurityUtil;
import com.assu.server.domain.auth.security.common.CommonUsernamePasswordAuthenticationToken;
import com.assu.server.domain.auth.security.student.StudentUsernamePasswordAuthenticationToken;
import com.assu.server.global.apiPayload.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final CommonAuthRepository commonAuthRepository;
    private final SSUAuthRepository ssuAuthRepository;
    private final MemberRepository memberRepository;

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Override
    public LoginResponse login(LoginRequest request) {
        // 공통(파트너/관리자) 로그인: 이메일/비번
        Authentication auth = authenticationManager.authenticate(
                new CommonUsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        CommonAuth commonAuth = commonAuthRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new CustomAuthException(ErrorStatus.NO_SUCH_MEMBER));

        Member member = commonAuth.getMember();
        Tokens tokens = jwtUtil.issueAndPersistTokens(member, member.getRole());

        return LoginResponse.builder()
                .memberId(member.getId())
                .role(member.getRole())
                .status(member.getIsActivated())
                .tokens(tokens)
                .build();
    }

    @Override
    public LoginResponse loginStudent(StudentLoginRequest request) {
        // 학생 로그인: 학번/학교 비번
        Authentication auth = authenticationManager.authenticate(
                new StudentUsernamePasswordAuthenticationToken(request.getStudentNumber(), request.getStudentPassword())
        );

        SSUAuth ssuAuth = ssuAuthRepository.findByStudentNumber(auth.getName())
                .orElseThrow(() -> new CustomAuthException(ErrorStatus.NO_SUCH_MEMBER));

        Member member = ssuAuth.getMember();
        Tokens tokens = jwtUtil.issueAndPersistTokens(member, member.getRole());

        return LoginResponse.builder()
                .memberId(member.getId())
                .role(member.getRole())
                .status(member.getIsActivated())
                .tokens(tokens)
                .build();
    }

    @Override
    public RefreshResponse refresh(String refreshToken) {
        Long userId = SecurityUtil.getCurrentUserId();
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new CustomAuthException(ErrorStatus.NO_SUCH_MEMBER));

        jwtUtil.validateRefreshToken(refreshToken);
        String savedRt = member.getRefreshToken();
        if (savedRt == null || !savedRt.equals(refreshToken)) {
            throw new CustomAuthException(ErrorStatus.REFRESH_TOKEN_NOT_EQUAL);
        }

        // 회전: JwtUtil에 위임(내부의 valid-seconds 사용)
        Tokens rotated = jwtUtil.issueAndPersistTokens(member, member.getRole());

        return new RefreshResponse(
                userId,
                rotated.getAccessToken(),
                rotated.getRefreshToken()
        );
    }
}