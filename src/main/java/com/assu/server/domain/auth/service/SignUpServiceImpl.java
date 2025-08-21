package com.assu.server.domain.auth.service;

import com.assu.server.domain.admin.entity.Admin;
import com.assu.server.domain.admin.repository.AdminRepository;
import com.assu.server.domain.auth.dto.signup.*;
import com.assu.server.domain.auth.dto.signup.common.CommonInfoPayload;
import com.assu.server.domain.auth.dto.signup.student.StudentInfoPayload;
import com.assu.server.domain.auth.entity.AuthRealm;
import com.assu.server.domain.auth.exception.CustomAuthHandler;
import com.assu.server.domain.auth.security.adapter.RealmAuthAdapter;
import com.assu.server.domain.auth.security.jwt.JwtUtil;
import com.assu.server.domain.common.enums.ActivationStatus;
import com.assu.server.domain.common.enums.UserRole;
import com.assu.server.domain.member.entity.Member;
import com.assu.server.domain.member.repository.MemberRepository;
import com.assu.server.domain.partner.entity.Partner;
import com.assu.server.domain.partner.repository.PartnerRepository;
import com.assu.server.domain.user.entity.Student;
import com.assu.server.domain.user.entity.enums.Major;
import com.assu.server.domain.user.repository.StudentRepository;
import com.assu.server.global.apiPayload.code.status.ErrorStatus;
import com.assu.server.infra.s3.AmazonS3Manager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


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

    private RealmAuthAdapter pickAdapter(AuthRealm realm) {
        return realmAuthAdapters.stream()
                .filter(a -> a.supports(realm))
                .findFirst()
                .orElseThrow(() -> new CustomAuthHandler(ErrorStatus.AUTHORIZATION_EXCEPTION));
    }

    /* 학생: JSON */
    @Override
    @Transactional
    public SignUpResponse signupStudent(StudentSignUpRequest req) {
        // 중복 체크
        if (memberRepository.existsByPhoneNum(req.getPhoneNumber())) {
            throw new CustomAuthHandler(ErrorStatus.EXISTED_PHONE);
        }

        // 1) member 생성
        Member member = memberRepository.save(
                Member.builder()
                        .phoneNum(req.getPhoneNumber())
                        .isPhoneVerified(true)
                        .role(UserRole.STUDENT)
                        .isActivated(ActivationStatus.ACTIVE)
                        .build()
        );

        // 2) RealmAuthAdapter 로 자격 저장 (학교별 암호화 전략 반영됨)
        RealmAuthAdapter adapter = pickAdapter(AuthRealm.valueOf(req.getStudentInfo().getUniversity().toString()));
        adapter.registerCredentials(member, req.getStudentAuth().getStudentNumber(), req.getStudentAuth().getStudentPassword());

        // 3) Student 프로필 생성
        StudentInfoPayload info = req.getStudentInfo();
        Major major;
        switch (info.getMajor()) {
            case "컴퓨터학부" -> major = Major.COM;
            case "소프트웨어학부" -> major = Major.SW;
            case "글로벌미디어학부" -> major = Major.GM;
            case "미디어경영학과" -> major = Major.MB;
            case "AI융합학부" -> major = Major.AI;
            case "전자정보공학부" -> major = Major.EE;
            case "정보보호학과" -> major = Major.IP;
            default -> major = null;
        }

        studentRepository.save(
                Student.builder()
                        .member(member)
                        .department(info.getDepartment())
                        .enrollmentStatus(info.getEnrollmentStatus())
                        .yearSemester(info.getYearSemester())
                        .university(info.getUniversity())
                        .stamp(0)
                        .major(major)
                        .build()
        );

        // 4) 토큰 발급
        Tokens tokens = jwtUtil.issueTokens(
                member.getId(),
                req.getStudentAuth().getStudentNumber(),
                UserRole.STUDENT,
                adapter.authRealmValue()
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
            throw new CustomAuthHandler(ErrorStatus.EXISTED_PHONE);
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

        // 3) Partner 프로필 생성
        partnerRepository.save(
                Partner.builder()
                        .member(member)
                        .name(info.getName())
                        .address(info.getAddress())
                        .detailAddress(info.getDetailAddress())
                        .licenseUrl(licenseUrl)
                        .build()
        );

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
            throw new CustomAuthHandler(ErrorStatus.EXISTED_PHONE);
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

        // 3) Partner 프로필 생성
        adminRepository.save(
                Admin.builder()
                        .member(member)
                        .name(info.getName())
                        .officeAddress(info.getAddress())
                        .detailAddress(info.getDetailAddress())
                        .signUrl(signUrl)
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
}