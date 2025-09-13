package com.assu.server.domain.auth.service;

import com.assu.server.domain.admin.entity.Admin;
import com.assu.server.domain.admin.repository.AdminRepository;
import com.assu.server.domain.auth.dto.signup.*;
import com.assu.server.domain.auth.dto.signup.common.CommonInfoPayload;
import com.assu.server.domain.auth.dto.ssu.USaintAuthRequest;
import com.assu.server.domain.auth.dto.ssu.USaintAuthResponse;
import com.assu.server.domain.auth.entity.AuthRealm;
import com.assu.server.domain.auth.exception.CustomAuthException;
import com.assu.server.domain.auth.repository.SSUAuthRepository;
import com.assu.server.domain.auth.security.adapter.RealmAuthAdapter;
import com.assu.server.domain.auth.security.jwt.JwtUtil;
import com.assu.server.domain.common.enums.ActivationStatus;
import com.assu.server.domain.common.enums.UserRole;
import com.assu.server.domain.member.entity.Member;
import com.assu.server.domain.member.repository.MemberRepository;
import com.assu.server.domain.partner.entity.Partner;
import com.assu.server.domain.partner.repository.PartnerRepository;
import com.assu.server.domain.store.entity.Store;
import com.assu.server.domain.store.repository.StoreRepository;
import com.assu.server.domain.user.entity.Student;
import com.assu.server.domain.user.entity.enums.EnrollmentStatus;
import com.assu.server.domain.user.entity.enums.University;
import com.assu.server.domain.user.repository.StudentRepository;
import com.assu.server.global.apiPayload.code.status.ErrorStatus;
import com.assu.server.infra.s3.AmazonS3Manager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SignUpServiceImpl implements SignUpService {

    private final MemberRepository memberRepository;
    private final StudentRepository studentRepository;
    private final PartnerRepository partnerRepository;
    private final AdminRepository adminRepository;

    // Adapter 들을 주입받아서, signup 시에 사용
    private final List<RealmAuthAdapter> realmAuthAdapters;

    private final AmazonS3Manager amazonS3Manager;
    private final JwtUtil jwtUtil;

    private final GeometryFactory geometryFactory;
    private final StoreRepository storeRepository;
    private final SSUAuthService ssuAuthService;
    private final SSUAuthRepository ssuAuthRepository;

    private RealmAuthAdapter pickAdapter(AuthRealm realm) {
        return realmAuthAdapters.stream()
                .filter(a -> a.supports(realm))
                .findFirst()
                .orElseThrow(() -> new CustomAuthException(ErrorStatus.AUTHORIZATION_EXCEPTION));
    }

    /* 숭실대 학생: sToken, sIdno 기반 회원가입 */
    @Override
    @Transactional
    public SignUpResponse signupSsuStudent(StudentTokenSignUpRequest req) {
        // 중복 체크
        if (memberRepository.existsByPhoneNum(req.getPhoneNumber())) {
            throw new CustomAuthException(ErrorStatus.EXISTED_PHONE);
        }

        // 1) 유세인트 인증 및 학생 정보 추출
        USaintAuthRequest authRequest = USaintAuthRequest.builder()
                .sToken(req.getStudentTokenAuth().getSToken())
                .sIdno(req.getStudentTokenAuth().getSIdno())
                .build();

        USaintAuthResponse authResponse = ssuAuthService.uSaintAuth(authRequest);

        // 학번 중복 체크
        if (ssuAuthRepository.existsByStudentNumber(authResponse.getStudentNumber().toString())) {
                throw new CustomAuthException(ErrorStatus.EXISTED_STUDENT);
        }

        // 2) member 생성
        Member member = memberRepository.save(
                Member.builder()
                        .phoneNum(req.getPhoneNumber())
                        .isPhoneVerified(true)
                        .role(UserRole.STUDENT)
                        .isActivated(ActivationStatus.ACTIVE)
                        .build()
        );

        // 3) SSUAuth 생성 (학번만 저장)
        RealmAuthAdapter adapter = pickAdapter(AuthRealm.SSU);
        adapter.registerCredentials(member, authResponse.getStudentNumber().toString(), ""); // 더미 패스워드

        // 4) Student 프로필 생성 (크롤링된 정보 사용)
        Student student = Student.builder()
                .member(member)
                .name(authResponse.getName())
                .department(authResponse.getMajor().getDepartment())
                .major(authResponse.getMajor())
                .enrollmentStatus(parseEnrollmentStatus(authResponse.getEnrollmentStatus()))
                .yearSemester(authResponse.getYearSemester())
                .university(University.SSU) // 고정값
                .stamp(0)
                .build();

        studentRepository.save(student);

        // 5) JWT 토큰 발급
        Tokens tokens = jwtUtil.issueTokens(
                member.getId(),
                authResponse.getStudentNumber().toString(), // studentNumber
                UserRole.STUDENT,
                "SSU"
        );

        return SignUpResponse.builder()
                .memberId(member.getId())
                .role(UserRole.STUDENT)
                .status(member.getIsActivated())
                .tokens(tokens)
                .build();
    }

    /* 제휴업체: MULTIPART(payload JSON + licenseImage) */
    @Override
    @Transactional
    public SignUpResponse signupPartner(PartnerSignUpRequest req, MultipartFile licenseImage) {
        if (memberRepository.existsByPhoneNum(req.getPhoneNumber())) {
            throw new CustomAuthException(ErrorStatus.EXISTED_PHONE);
        }

        // 1) member 생성
        Member member = memberRepository.save(
                Member.builder()
                        .phoneNum(req.getPhoneNumber())
                        .isPhoneVerified(true)
                        .role(UserRole.PARTNER)
                        .isActivated(ActivationStatus.ACTIVE) // Todo 초기에 SUSPEND 로직 추가해야함, 허가 후 ACTIVE
                        .build()
        );

        // 2) RealmAuthAdapter 로 Common 자격 저장
        RealmAuthAdapter adapter = pickAdapter(AuthRealm.COMMON);
        adapter.registerCredentials(member, req.getCommonAuth().getEmail(), req.getCommonAuth().getPassword());

        // 파일 업로드 + 파트너 정보
        String keyPath = "partners/" + member.getId() + "/" + licenseImage.getOriginalFilename();
        String keyName = amazonS3Manager.generateKeyName(keyPath);
        String licenseUrl = amazonS3Manager.uploadFile(keyName, licenseImage);
        CommonInfoPayload info = req.getCommonInfo();
        var sp = Optional.ofNullable(info.getSelectedPlace())
                .orElseThrow(() -> new CustomAuthException(ErrorStatus._BAD_REQUEST)); // selectedPlace 필수

        // selectedPlace로부터 주소/좌표 생성
        String address = pickDisplayAddress(sp.getRoadAddress(), sp.getAddress());
        Double lat = sp.getLatitude();
        Double lng = sp.getLongitude();
        Point point = toPoint(lat, lng);

        // 3) Partner 프로필 생성
        Partner partner = partnerRepository.save(
                Partner.builder()
                        .member(member)
                        .name(info.getName())
                        .address(address)
                        .detailAddress(info.getDetailAddress())
                        .licenseUrl(licenseUrl)
                        .point(point)
                        .latitude(lat)
                        .longitude(lng)
                        .build()
        );

        // store 생성/연결
        Optional<Store> storeOpt = storeRepository.findBySameAddress(address, info.getDetailAddress());
        if (storeOpt.isPresent()) {
            Store store = storeOpt.get();
            store.linkPartner(partner);
            store.setName(info.getName());
            store.setGeo(lat, lng, point);
            storeRepository.save(store);
        } else {
            Store newly = Store.builder()
                    .partner(partner)
                    .rate(0)
                    .isActivate(ActivationStatus.ACTIVE)
                    .name(info.getName())
                    .address(address)
                    .detailAddress(info.getDetailAddress())
                    .latitude(lat)
                    .longitude(lng)
                    .point(point)
                    .build();
            storeRepository.save(newly);
        }

        // 4) 토큰 발급
        Tokens tokens = jwtUtil.issueTokens(
                member.getId(),
                req.getCommonAuth().getEmail(),
                UserRole.PARTNER,
                adapter.authRealmValue()
        );

        return SignUpResponse.builder()
                .memberId(member.getId())
                .role(UserRole.PARTNER)
                .status(member.getIsActivated())
                .tokens(tokens)
                .build();
    }

    /* 관리자: MULTIPART(payload JSON + signImage) */
    @Override
    @Transactional
    public SignUpResponse signupAdmin(AdminSignUpRequest req, MultipartFile signImage) {
        if (memberRepository.existsByPhoneNum(req.getPhoneNumber())) {
            throw new CustomAuthException(ErrorStatus.EXISTED_PHONE);
        }

        // 1) member 생성
        Member member = memberRepository.save(
                Member.builder()
                        .phoneNum(req.getPhoneNumber())
                        .isPhoneVerified(true)
                        .role(UserRole.ADMIN)
                        .isActivated(ActivationStatus.ACTIVE) // Todo 초기에 SUSPEND 로직 추가해야함, 허가 후 ACTIVE
                        .build()
        );

        // 2) RealmAuthAdapter 로 Common 자격 저장
        RealmAuthAdapter adapter = pickAdapter(AuthRealm.COMMON);
        adapter.registerCredentials(member, req.getCommonAuth().getEmail(), req.getCommonAuth().getPassword());

        // 파일 업로드 + 관리자 정보
        String keyPath = "admins/" + member.getId() + "/" + signImage.getOriginalFilename();
        String keyName = amazonS3Manager.generateKeyName(keyPath);
        String signUrl = amazonS3Manager.uploadFile(keyName, signImage);
        CommonInfoPayload info = req.getCommonInfo();
        var sp = Optional.ofNullable(info.getSelectedPlace())
                .orElseThrow(() -> new CustomAuthException(ErrorStatus._BAD_REQUEST)); // selectedPlace 필수

        // selectedPlace로부터 주소/좌표 생성
        String address = pickDisplayAddress(sp.getRoadAddress(), sp.getAddress());
        Double lat = sp.getLatitude();
        Double lng = sp.getLongitude();
        Point point = toPoint(lat, lng);

        // 3) Partner 프로필 생성
        adminRepository.save(
                Admin.builder()
                    .major(req.getCommonAuth().getMajor())
                    .department(req.getCommonAuth().getDepartment())
                    .university(req.getCommonAuth().getUniversity())
                        .member(member)
                        .name(info.getName())
                        .officeAddress(address)
                        .detailAddress(info.getDetailAddress())
                        .signUrl(signUrl)
                        .point(point)
                        .latitude(lat)
                        .longitude(lng)
                        .build()
        );

        // 4) 토큰 발급
        Tokens tokens = jwtUtil.issueTokens(
                member.getId(),
                req.getCommonAuth().getEmail(),
                UserRole.ADMIN,
                adapter.authRealmValue()
        );

        return SignUpResponse.builder()
                .memberId(member.getId())
                .role(UserRole.ADMIN)
                .status(member.getIsActivated())
                .tokens(tokens)
                .build();
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

    public Point toPoint(Double lat, Double lng) {
        if (lat == null || lng == null) return null;
        Point p = geometryFactory.createPoint(new Coordinate(lng, lat)); // x=lng, y=lat
        p.setSRID(4326);
        return p;
    }

    private String pickDisplayAddress(String road, String jibun) {
        return (road != null && !road.isBlank()) ? road : jibun;
    }
}