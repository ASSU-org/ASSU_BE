package com.assu.server.domain.auth.service;

import com.assu.server.domain.auth.dto.login.CommonLoginRequest;
import com.assu.server.domain.auth.dto.login.LoginResponse;
import com.assu.server.domain.auth.dto.login.RefreshResponse;
import com.assu.server.domain.auth.dto.login.StudentLoginRequest;
import com.assu.server.domain.auth.dto.signup.Tokens;
import com.assu.server.domain.auth.entity.AuthRealm;
import com.assu.server.domain.auth.security.adapter.RealmAuthAdapter;
import com.assu.server.domain.auth.security.token.LoginUsernamePasswordAuthenticationToken;
import com.assu.server.domain.member.entity.Member;
import com.assu.server.domain.auth.exception.CustomAuthHandler;
import com.assu.server.domain.auth.security.jwt.JwtUtil;
import com.assu.server.global.apiPayload.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    // 공통/학생/기타 학교까지 모두 여기로 주입
    private final List<RealmAuthAdapter> realmAuthAdapters;

    private RealmAuthAdapter pickAdapter(AuthRealm realm) {
        return realmAuthAdapters.stream()
                .filter(a -> a.supports(realm))
                .findFirst()
                .orElseThrow(() -> new CustomAuthHandler(ErrorStatus.AUTHORIZATION_EXCEPTION));
    }

    /**
     * 공통(파트너/관리자) 로그인: 이메일/비밀번호 기반.
     * 1) 인증 성공 시 CommonAuth 조회
     * 2) JWT 발급: username=email, authRealm=COMMON
     */
    @Override
    public LoginResponse loginCommon(CommonLoginRequest request) {
        // 공통(파트너/관리자) 로그인: 이메일/비번
        Authentication authentication = authenticationManager.authenticate(
                new LoginUsernamePasswordAuthenticationToken(
                        AuthRealm.COMMON,
                        request.getEmail(),
                        request.getPassword()
                )
        );

        RealmAuthAdapter adapter = pickAdapter(AuthRealm.COMMON);

        // identifier = email
        Member member = adapter.loadMember(authentication.getName());

        // 토큰 발급 (Access 미저장, Refresh는 Redis 저장)
        Tokens tokens = jwtUtil.issueTokens(
                member.getId(),
                authentication.getName(), // email
                member.getRole(),
                adapter.authRealmValue()    // "COMMON"
        );

        return LoginResponse.builder()
                .memberId(member.getId())
                .role(member.getRole())
                .status(member.getIsActivated())
                .tokens(tokens)
                .build();
    }

    /**
     * 학생 로그인: 학번/학교 비밀번호 기반.
     * 1) 인증 성공 시 SSUAuth 조회
     * 2) JWT 발급: username=studentNumber, authRealm=SSU
     */
    @Override
    public LoginResponse loginStudent(StudentLoginRequest request) {

        String realmStr = request.getUniversity().toString();  // University → AuthRealm 매핑
        AuthRealm authRealm = AuthRealm.valueOf(realmStr);
        RealmAuthAdapter adapter = pickAdapter(authRealm);

        Authentication authentication = authenticationManager.authenticate(
                new LoginUsernamePasswordAuthenticationToken(
                        authRealm,
                        request.getStudentNumber(),
                        request.getStudentPassword()
                )
        );

        // identifier = studentNumber
        Member member = adapter.loadMember(authentication.getName());

        // 토큰 발급 (Access 미저장, Refresh는 Redis 저장)
        Tokens tokens = jwtUtil.issueTokens(
                member.getId(),
                authentication.getName(), // studentNumber
                member.getRole(),
                adapter.authRealmValue()    // 예: "SSU"
        );

        return LoginResponse.builder()
                .memberId(member.getId())
                .role(member.getRole())
                .status(member.getIsActivated())
                .tokens(tokens)
                .build();
    }

    /**
     * Refresh 토큰 재발급(회전).
     * 전제: JwtAuthFilter가 /auth/refresh 에서 Access(만료 허용) 서명 검증 및 컨텍스트 세팅을 이미 수행.
     *
     * 절차:
     * 1) RT 서명/만료 검증(jwtUtil.validateRefreshToken)
     * 2) RT의 Claims 추출(만료 X), memberId/jti/username/role/authRealm 획득
     * 3) Redis 키 "refresh:{memberId}:{jti}" 존재 및 값 일치 확인(도난/중복 재사용 차단)
     * 4) 기존 RT 키 삭제(회전), 새 토큰 발급(issueTokens)
     */
    @Override
    public RefreshResponse refresh(String refreshToken) {
        Tokens rotated = jwtUtil.rotateRefreshToken(refreshToken);
        return new RefreshResponse(
                ((Number) jwtUtil.validateTokenOnlySignature(rotated.getAccessToken()).get("userId")).longValue(),
                rotated.getAccessToken(),
                rotated.getRefreshToken()
        );
    }
}