package com.assu.server.domain.auth.service;

import com.assu.server.domain.admin.entity.Admin;
import com.assu.server.domain.admin.repository.AdminRepository;
import com.assu.server.domain.auth.crypto.SchoolCredentialEncryptor;
import com.assu.server.domain.auth.dto.signup.*;
import com.assu.server.domain.auth.dto.signup.common.CommonInfoPayload;
import com.assu.server.domain.auth.dto.signup.student.StudentInfoPayload;
import com.assu.server.domain.auth.entity.CommonAuth;
import com.assu.server.domain.auth.entity.SSUAuth;
import com.assu.server.domain.auth.exception.CustomAuthException;
import com.assu.server.domain.auth.repository.CommonAuthRepository;
import com.assu.server.domain.auth.repository.SSUAuthRepository;
import com.assu.server.domain.auth.security.JwtUtil;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
@RequiredArgsConstructor
public class SignUpServiceImpl implements SignUpService {

    private final MemberRepository memberRepository;
    private final SSUAuthRepository ssuAuthRepository;
    private final CommonAuthRepository commonAuthRepository;
    private final StudentRepository studentRepository;
    private final PartnerRepository partnerRepository;
    private final AdminRepository adminRepository;

    private final PasswordEncoder passwordEncoder;           // 공통(파트너/관리자)용 BCrypt
    private final SchoolCredentialEncryptor schoolEncryptor; // 학생용 AES-GCM
    private final AmazonS3Manager amazonS3Manager;
    private final JwtUtil jwtUtil;

    /* 학생: JSON */
    @Override
    @Transactional
    public SignUpResponse signupStudent(StudentSignUpRequest req) {
        // 중복 체크
        if (memberRepository.existsByPhoneNum(req.getPhoneNumber())) {
            throw new CustomAuthException(ErrorStatus.EXISTED_PHONE);
        }
        if (ssuAuthRepository.existsByStudentNumber(req.getStudentAuth().getStudentNumber())) {
            throw new CustomAuthException(ErrorStatus.EXISTED_STUDENT);
        }

        // member 생성
        Member member = memberRepository.save(
                Member.builder()
                        .phoneNum(req.getPhoneNumber())
                        .isPhoneVerified(true)
                        .role(UserRole.STUDENT)
                        .isActivated(ActivationStatus.ACTIVE)
                        .build()
        );

        // ssu_auth 생성(학생번호/암호화 PW(AES-GCM))
        String cipher = schoolEncryptor.encrypt(req.getStudentAuth().getStudentPassword());
        ssuAuthRepository.save(
                SSUAuth.builder()
                        .member(member)
                        .studentNumber(req.getStudentAuth().getStudentNumber())
                        .passwordCipher(cipher)
                        .isAuthenticated(true) // 초기값(유세인트 검증 완료 여부에 맞게 조정)
                        .build()
        );

        // student 프로필 생성
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

        // JWT 발급 및 저장
        Tokens tokens = jwtUtil.issueAndPersistTokens(member, UserRole.STUDENT);

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
        if (commonAuthRepository.existsByEmail(req.getCommonAuth().getEmail())) {
            throw new CustomAuthException(ErrorStatus.EXISTED_EMAIL);
        }

        Member member = memberRepository.save(
                Member.builder()
                        .phoneNum(req.getPhoneNumber())
                        .isPhoneVerified(true)
                        .role(UserRole.PARTNER)
                        .isActivated(ActivationStatus.SUSPEND) // 사업자 등록증 확인 후 활성화
                        .build()
        );

        // CommonAuth: BCrypt 해시 저장
        String pwHash = passwordEncoder.encode(req.getCommonAuth().getPassword());
        commonAuthRepository.save(
                CommonAuth.builder()
                        .member(member)
                        .email(req.getCommonAuth().getEmail())
                        .password(pwHash)
                        .isEmailVerified(false)
                        .build()
        );

        // 파일 업로드 + 파트너 정보
        String keyPath = "partners/" + member.getId() + "/" + licenseImage.getOriginalFilename();
        String keyName = amazonS3Manager.generateKeyName(keyPath);
        String licenseUrl = amazonS3Manager.uploadFile(keyName, licenseImage);

        CommonInfoPayload info = req.getCommonInfo();
        partnerRepository.save(
                Partner.builder()
                        .member(member)
                        .name(info.getName())
                        .address(info.getAddress())
                        .detailAddress(info.getDetailAddress())
                        .licenseUrl(licenseUrl)
                        .build()
        );

        Tokens tokens = jwtUtil.issueAndPersistTokens(member, UserRole.PARTNER);

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
        if (commonAuthRepository.existsByEmail(req.getCommonAuth().getEmail())) {
            throw new CustomAuthException(ErrorStatus.EXISTED_EMAIL);
        }

        Member member = memberRepository.save(
                Member.builder()
                        .phoneNum(req.getPhoneNumber())
                        .isPhoneVerified(true)
                        .role(UserRole.ADMIN)
                        .isActivated(ActivationStatus.SUSPEND) // 인감 확인 후 활성화
                        .build()
        );

        String pwHash = passwordEncoder.encode(req.getCommonAuth().getPassword());
        commonAuthRepository.save(
                CommonAuth.builder()
                        .member(member)
                        .email(req.getCommonAuth().getEmail())
                        .password(pwHash)
                        .isEmailVerified(false)
                        .build()
        );

        String keyPath = "admins/" + member.getId() + "/" + signImage.getOriginalFilename();
        String keyName = amazonS3Manager.generateKeyName(keyPath);
        String signUrl = amazonS3Manager.uploadFile(keyName, signImage);

        CommonInfoPayload info = req.getCommonInfo();
        adminRepository.save(
                Admin.builder()
                        .member(member)
                        .name(info.getName())
                        .officeAddress(info.getAddress())
                        .detailAddress(info.getDetailAddress())
                        .signUrl(signUrl)
                        .build()
        );

        Tokens tokens = jwtUtil.issueAndPersistTokens(member, UserRole.ADMIN);

        return SignUpResponse.builder()
                .memberId(member.getId())
                .role(UserRole.ADMIN)
                .status(member.getIsActivated())
                .tokens(tokens)
                .build();
    }
}