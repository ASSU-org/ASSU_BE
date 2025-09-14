package com.assu.server.domain.auth.service;

import com.assu.server.domain.auth.dto.common.UserBasicInfo;
import com.assu.server.domain.auth.dto.login.CommonLoginRequest;
import com.assu.server.domain.auth.dto.login.LoginResponse;
import com.assu.server.domain.auth.dto.login.RefreshResponse;
import com.assu.server.domain.auth.dto.signup.student.StudentTokenAuthPayload;
import com.assu.server.domain.auth.dto.ssu.USaintAuthRequest;
import com.assu.server.domain.auth.dto.ssu.USaintAuthResponse;
import com.assu.server.domain.auth.dto.signup.Tokens;
import com.assu.server.domain.auth.entity.AuthRealm;
import com.assu.server.domain.auth.security.adapter.RealmAuthAdapter;
import com.assu.server.domain.auth.security.token.LoginUsernamePasswordAuthenticationToken;
import com.assu.server.domain.member.entity.Member;
import com.assu.server.domain.auth.exception.CustomAuthException;
import com.assu.server.domain.auth.security.jwt.JwtUtil;
import com.assu.server.domain.user.entity.Student;
import com.assu.server.domain.user.entity.enums.Department;
import com.assu.server.domain.user.entity.enums.EnrollmentStatus;
import com.assu.server.domain.user.entity.enums.Major;
import com.assu.server.domain.user.entity.enums.University;
import com.assu.server.domain.user.repository.StudentRepository;
import com.assu.server.global.apiPayload.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final SSUAuthService ssuAuthService;
    private final StudentRepository studentRepository;

    // 공통/학생/기타 학교까지 모두 여기로 주입
    private final List<RealmAuthAdapter> realmAuthAdapters;

    private RealmAuthAdapter pickAdapter(AuthRealm realm) {
        return realmAuthAdapters.stream()
                .filter(a -> a.supports(realm))
                .findFirst()
                .orElseThrow(() -> new CustomAuthException(ErrorStatus.AUTHORIZATION_EXCEPTION));
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
                        request.getPassword()));

        RealmAuthAdapter adapter = pickAdapter(AuthRealm.COMMON);

        // identifier = email
        Member member = adapter.loadMember(authentication.getName());

        // 토큰 발급 (Access 미저장, Refresh는 Redis 저장)
        Tokens tokens = jwtUtil.issueTokens(
                member.getId(),
                authentication.getName(), // email
                member.getRole(),
                adapter.authRealmValue() // "COMMON"
        );

        return LoginResponse.builder()
                .memberId(member.getId())
                .role(member.getRole())
                .status(member.getIsActivated())
                .tokens(tokens)
                .basicInfo(buildUserBasicInfo(member))
                .build();
    }

    /**
     * 숭실대 학생 로그인: sToken, sIdno 기반.
     * 1) 유세인트 인증으로 학생 정보 확인
     * 2) 기존 회원 확인
     * 3) Student 정보 업데이트 (유세인트에서 크롤링한 최신 정보로)
     * 4) JWT 발급: username=studentNumber, authRealm=SSU
     */
    @Override
    @Transactional
    public LoginResponse loginSsuStudent(StudentTokenAuthPayload request) {
        // 1) 유세인트 인증
        USaintAuthRequest authRequest = USaintAuthRequest.builder()
                .sToken(request.getSToken())
                .sIdno(request.getSIdno())
                .build();

        USaintAuthResponse authResponse = ssuAuthService.uSaintAuth(authRequest);

        // 2) 기존 회원 확인
        String realmStr = request.getUniversity().toString();
        AuthRealm authRealm = AuthRealm.valueOf(realmStr);
        RealmAuthAdapter adapter = pickAdapter(authRealm);

        Member member = adapter.loadMember(authResponse.getStudentNumber().toString());

        // 3) Student 정보 업데이트 (유세인트에서 크롤링한 최신 정보로)
        Student student = member.getStudentProfile();
        if (student == null) {
            throw new CustomAuthException(ErrorStatus.NO_SUCH_MEMBER);
        }

        // 유세인트에서 크롤링한 최신 정보로 업데이트
        student.updateStudentInfo(
                authResponse.getName(),
                authResponse.getMajor(),
                parseEnrollmentStatus(authResponse.getEnrollmentStatus()),
                authResponse.getYearSemester());

        studentRepository.save(student);

        // 4) 토큰 발급
        Tokens tokens = jwtUtil.issueTokens(
                member.getId(),
                authResponse.getStudentNumber().toString(), // studentNumber
                member.getRole(),
                adapter.authRealmValue() // 예: "SSU"
        );

        return LoginResponse.builder()
                .memberId(member.getId())
                .role(member.getRole())
                .status(member.getIsActivated())
                .tokens(tokens)
                .basicInfo(buildUserBasicInfo(member))
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
                rotated.getRefreshToken());
    }

    private EnrollmentStatus parseEnrollmentStatus(String status) {
        if (status == null || status.isBlank()) {
            return EnrollmentStatus.ENROLLED;
        }
        if (status.contains("재학")) {
            return EnrollmentStatus.ENROLLED;
        } else if (status.contains("휴학")) {
            return EnrollmentStatus.LEAVE;
        } else if (status.contains("졸업")) {
            return EnrollmentStatus.GRADUATED;
        } else {
            // 기본값은 재학으로 설정
            return EnrollmentStatus.ENROLLED;
        }
    }

    /**
     * 사용자 기본 정보를 빌드하는 헬퍼 메서드
     */
    private UserBasicInfo buildUserBasicInfo(Member member) {
        UserBasicInfo.UserBasicInfoBuilder builder = UserBasicInfo.builder();

        switch (member.getRole()) {
            case STUDENT -> {
                Student student = member.getStudentProfile();
                if (student != null) {
                    builder.name(student.getName())
                            .university(student.getUniversity().getDisplayName())
                            .department(student.getDepartment().getDisplayName())
                            .major(student.getMajor().getDisplayName());
                }
            }
            case ADMIN -> {
                // Admin 엔티티에서 정보 추출
                var admin = member.getAdminProfile();
                if (admin != null) {
                    builder.name(admin.getName())
                            .university(admin.getUniversity() != null ? admin.getUniversity().getDisplayName() : null)
                            .department(admin.getDepartment() != null ? admin.getDepartment().getDisplayName() : null)
                            .major(admin.getMajor() != null ? admin.getMajor().getDisplayName() : null);
                }
            }
            case PARTNER -> {
                // Partner 엔티티에서 정보 추출 (Partner는 name만 필요)
                var partner = member.getPartnerProfile();
                if (partner != null) {
                    builder.name(partner.getName());
                }
            }
        }

        return builder.build();
    }
}