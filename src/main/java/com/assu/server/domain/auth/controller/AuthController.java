package com.assu.server.domain.auth.controller;

import com.assu.server.domain.auth.dto.login.CommonLoginRequest;
import com.assu.server.domain.auth.dto.login.LoginResponse;
import com.assu.server.domain.auth.dto.login.RefreshResponse;
import com.assu.server.domain.auth.dto.login.StudentLoginRequest;
import com.assu.server.domain.auth.dto.phone.PhoneAuthRequestDTO;
import com.assu.server.domain.auth.dto.signup.AdminSignUpRequest;
import com.assu.server.domain.auth.dto.signup.PartnerSignUpRequest;
import com.assu.server.domain.auth.dto.signup.SignUpResponse;
import com.assu.server.domain.auth.dto.signup.StudentSignUpRequest;
import com.assu.server.domain.auth.dto.ssu.USaintAuthRequest;
import com.assu.server.domain.auth.dto.ssu.USaintAuthResponse;
import com.assu.server.domain.auth.service.*;
import com.assu.server.domain.common.enums.UserRole;
import com.assu.server.global.apiPayload.BaseResponse;
import com.assu.server.global.apiPayload.code.status.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Auth", description = "인증/인가 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final PhoneAuthService phoneAuthService;
    private final SignUpService signUpService;
    private final LoginService loginService;
    private final LogoutService logoutService;
    private final SSUAuthService ssuAuthService;

    @Operation(
            summary = "휴대폰 인증번호 발송 API",
            description = "# [v1.0 (2025-09-03)](https://clumsy-seeder-416.notion.site/2241197c19ed801bbcd9f61c3e5f5457?source=copy_link)\n" +
                    "- 입력한 휴대폰 번호로 1회용 인증번호(OTP)를 발송합니다.\n" +
                    "- 유효시간/재요청 제한 정책은 서버 설정에 따릅니다.\n" +
                    "\n**Request Body:**\n" +
                    "  - `phoneNumber` (String, required): 인증번호를 받을 휴대폰 번호\n" +
                    "\n**Response:**\n" +
                    "  - 성공 시 200(OK)과 성공 메시지 반환"
    )
    @PostMapping("/phone-verification/send")
    public BaseResponse<Void> sendAuthNumber(
            @RequestBody @Valid PhoneAuthRequestDTO.PhoneAuthSendRequest request
    ) {
        phoneAuthService.sendAuthNumber(request.getPhoneNumber());
        return BaseResponse.onSuccess(SuccessStatus.SEND_AUTH_NUMBER_SUCCESS, null);
    }

    @Operation(
            summary = "휴대폰 인증번호 검증 API",
            description = "# [v1.0 (2025-09-03)](https://clumsy-seeder-416.notion.site/2241197c19ed81bb8c05d9061c0306c0?source=copy_link)\n" +
                    "- 발송된 인증번호(OTP)를 검증합니다.\n" +
                    "- 성공 시 서버에 휴대폰 인증 상태가 기록됩니다.\n" +
                    "\n**Request Body:**\n" +
                    "  - `phoneNumber` (String, required): 인증받을 휴대폰 번호\n" +
                    "  - `authNumber` (String, required): 발송받은 인증번호(OTP)\n" +
                    "\n**Response:**\n" +
                    "  - 성공 시 200(OK)과 성공 메시지 반환"
    )
    @PostMapping("/phone-verification/verify")
    public BaseResponse<Void> checkAuthNumber(
            @RequestBody @Valid PhoneAuthRequestDTO.PhoneAuthVerifyRequest request
    ) {
        phoneAuthService.verifyAuthNumber(
                request.getPhoneNumber(),
                request.getAuthNumber()
        );
        return BaseResponse.onSuccess(SuccessStatus.VERIFY_AUTH_NUMBER_SUCCESS, null);
    }

    @Operation(
            summary = "학생 회원가입 API",
            description = "# [v1.0 (2025-09-03)](https://clumsy-seeder-416.notion.site/2241197c19ed81129c85cf5bbe1f7971?source=copy_link)\n" +
                    "- `application/json` 요청 바디를 사용합니다.\n" +
                    "- 처리: users + ssu_auth 등 가입 레코드 생성, 휴대폰 인증 여부 확인.\n" +
                    "- 성공 시 201(Created)과 생성된 memberId 반환.\n" +
                    "\n**Request Body:**\n" +
                    "  - `StudentSignUpRequest` 객체 (JSON, required): 학생 가입 정보\n" +
                    "  - `email` (String, required): 이메일 주소\n" +
                    "  - `password` (String, required): 비밀번호\n" +
                    "  - `phoneNumber` (String, required): 휴대폰 번호\n" +
                    "  - `studentNumber` (String, required): 학번\n" +
                    "  - `name` (String, required): 학생 이름\n" +
                    "  - `major` (Major enum, required): 전공\n" +
                    "  - `grade` (Integer, required): 학년\n" +
                    "  - `semester` (Integer, required): 학기\n" +
                    "\n**Response:**\n" +
                    "  - 성공 시 201(Created)과 `SignUpResponse` 객체 반환"
    )
    @PostMapping(value = "/students/signup", consumes = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<SignUpResponse> signupStudent(
            @Valid @RequestBody StudentSignUpRequest request) {
        return BaseResponse.onSuccess(SuccessStatus._OK, signUpService.signupStudent(request));
    }

    @Operation(
            summary = "제휴업체 회원가입 API",
            description = "# [v1.0 (2025-09-03)](https://clumsy-seeder-416.notion.site/2501197c19ed80d7a8f2c3a6fcd8b537?source=copy_link)\n" +
                    "- `multipart/form-data`로 호출합니다.\n" +
                    "- 파트: `payload`(JSON, PartnerSignUpRequest) + `licenseImage`(파일, 사업자등록증).\n" +
                    "- 처리: users + common_auth 생성, 이메일 중복/비밀번호 규칙 검증.\n" +
                    "- 성공 시 201(Created)과 생성된 memberId 반환.\n" +
                    "\n**Request Parts:**\n" +
                    "  - `request` (JSON, required): `PartnerSignUpRequest` 객체\n" +
                    "  - `email` (String, required): 이메일 주소\n" +
                    "  - `password` (String, required): 비밀번호\n" +
                    "  - `phoneNumber` (String, required): 휴대폰 번호\n" +
                    "  - `companyName` (String, required): 회사명\n" +
                    "  - `businessNumber` (String, required): 사업자등록번호\n" +
                    "  - `representativeName` (String, required): 대표자명\n" +
                    "  - `address` (String, required): 회사 주소\n" +
                    "  - `licenseImage` (MultipartFile, required): 사업자등록증 이미지 파일\n" +
                    "\n**Response:**\n" +
                    "  - 성공 시 201(Created)과 `SignUpResponse` 객체 반환"
    )
    @PostMapping(value = "/partners/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse<SignUpResponse> signupPartner(
            @Valid @RequestPart("request")
            @Parameter(
                    description = "JSON 형식의 제휴업체 가입 정보",
                    // 'request' 파트의 content type을 명시적으로 지정
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PartnerSignUpRequest.class))
            )
            PartnerSignUpRequest request,

            @RequestPart("licenseImage")
            @Parameter(
                    description = "사업자등록증 이미지 파일 (Multipart Part)",
                    required = true,
                    content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE,
                            schema = @Schema(type = "string", format = "binary"))
            )
            MultipartFile licenseImage
    ) {
        return BaseResponse.onSuccess(SuccessStatus._OK, signUpService.signupPartner(request, licenseImage));
    }

    @Operation(
            summary = "관리자 회원가입 API",
            description = "# [v1.0 (2025-09-03)](https://clumsy-seeder-416.notion.site/2501197c19ed80cdb98bc2b4d5042b48?source=copy_link)\n" +
                    "- `multipart/form-data`로 호출합니다.\n" +
                    "- 파트: `payload`(JSON, AdminSignUpRequest) + `signImage`(파일, 신분증).\n" +
                    "- 처리: users + common_auth 생성, 이메일 중복/비밀번호 규칙 검증.\n" +
                    "- 성공 시 201(Created)과 생성된 memberId 반환.\n" +
                    "\n**Request Parts:**\n" +
                    "  - `request` (JSON, required): `AdminSignUpRequest` 객체\n" +
                    "  - `email` (String, required): 이메일 주소\n" +
                    "  - `password` (String, required): 비밀번호\n" +
                    "  - `phoneNumber` (String, required): 휴대폰 번호\n" +
                    "  - `name` (String, required): 관리자 이름\n" +
                    "  - `department` (String, required): 소속 부서\n" +
                    "  - `position` (String, required): 직책\n" +
                    "  - `signImage` (MultipartFile, required): 인감 이미지 파일\n" +
                    "\n**Response:**\n" +
                    "  - 성공 시 201(Created)과 `SignUpResponse` 객체 반환"
    )
    @PostMapping(value = "/admins/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse<SignUpResponse> signupAdmin(
            @Valid @RequestPart("request")
            @Parameter(
                    description = "JSON 형식의 관리자 가입 정보",
                    // 'request' 파트의 content type을 명시적으로 지정
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AdminSignUpRequest.class))
            )
            AdminSignUpRequest request,
            @RequestPart("signImage")
            @Parameter(
                    description = "인감 이미지 파일 (Multipart Part)",
                    required = true,
                    content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE,
                            schema = @Schema(type = "string", format = "binary"))
            )
            MultipartFile signImage) {
        return BaseResponse.onSuccess(SuccessStatus._OK, signUpService.signupAdmin(request, signImage));
    }


    // 로그인 (파트너/관리자 공통)
    @Operation(
            summary = "공통 로그인 API",
            description = "# [v1.0 (2025-09-03)](https://clumsy-seeder-416.notion.site/2241197c19ed811c961be6a474de0e50?source=copy_link)\n" +
                    "- `application/json`로 호출합니다.\n" +
                    "- 바디: `LoginRequest(email, password)`.\n" +
                    "- 처리: 자격 증명 검증 후 Access/Refresh 토큰 발급 및 저장.\n" +
                    "- 성공 시 200(OK)과 토큰/만료시각 반환.\n" +
                    "\n**Request Body:**\n" +
                    "  - `CommonLoginRequest` 객체 (JSON, required): 로그인 정보\n" +
                    "  - `email` (String, required): 이메일 주소\n" +
                    "  - `password` (String, required): 비밀번호\n" +
                    "\n**Response:**\n" +
                    "  - 성공 시 200(OK)과 `LoginResponse` 객체 반환\n" +
                    "  - `accessToken` (String): 액세스 토큰\n" +
                    "  - `refreshToken` (String): 리프레시 토큰\n" +
                    "  - `expiresAt` (LocalDateTime): 토큰 만료 시각"
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(schema = @Schema(implementation = CommonLoginRequest.class))
    )
    @PostMapping(value = "/commons/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<LoginResponse> loginCommon(
            @RequestBody @Valid CommonLoginRequest request
    ) {
        return BaseResponse.onSuccess(SuccessStatus._OK, loginService.loginCommon(request));
    }


    // 학생 로그인
    @Operation(
            summary = "학생 로그인 API",
            description = "# [v1.0 (2025-09-03)](https://clumsy-seeder-416.notion.site/2501197c19ed80f6b495fa37f8c084a8?source=copy_link)\n" +
                    "- `application/json`로 호출합니다.\n" +
                    "- 바디: `바디: `StudentLoginRequest(studentNumber, studentPassword, school)`.\n" +
                    "- 처리: 자격 증명 검증 후 Access/Refresh 토큰 발급 및 저장.\n" +
                    "- 성공 시 200(OK)과 토큰/만료시각 반환.\n" +
                    "\n**Request Body:**\n" +
                    "  - `StudentLoginRequest` 객체 (JSON, required): 학생 로그인 정보\n" +
                    "  - `studentNumber` (String, required): 학번\n" +
                    "  - `studentPassword` (String, required): 학생 포털 비밀번호\n" +
                    "  - `school` (String, required): 학교명\n" +
                    "\n**Response:**\n" +
                    "  - 성공 시 200(OK)과 `LoginResponse` 객체 반환\n" +
                    "  - `accessToken` (String): 액세스 토큰\n" +
                    "  - `refreshToken` (String): 리프레시 토큰\n" +
                    "  - `expiresAt` (LocalDateTime): 토큰 만료 시각"
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(schema = @Schema(implementation = StudentLoginRequest.class))
    )
    @PostMapping(value = "/students/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<LoginResponse> loginStudent(
            @RequestBody @Valid StudentLoginRequest request
    ) {
        return BaseResponse.onSuccess(SuccessStatus._OK, loginService.loginStudent(request));
    }


    // 액세스 토큰 갱신
    @Operation(
            summary = "Access Token 갱신 API",
            description = "# [v1.0 (2025-09-03)](https://clumsy-seeder-416.notion.site/2501197c19ed806ea8cff29f9cd8695a?source=copy_link)\n" +
                    "- 헤더로 호출합니다.\n" +
                    "- 헤더: `Authorization: Bearer <accessToken>`(만료 허용), `RefreshToken: <refreshToken>`.\n" +
                    "- 처리: Refresh 검증/회전 후 신규 Access/Refresh 발급 및 저장.\n" +
                    "- 성공 시 200(OK)과 새 토큰/만료시각 반환.\n" +
                    "\n**Headers:**\n" +
                    "  - `Authorization` (String, required): Bearer 토큰 형식의 액세스 토큰 (만료 허용)\n" +
                    "  - `RefreshToken` (String, required): 리프레시 토큰\n" +
                    "\n**Response:**\n" +
                    "  - 성공 시 200(OK)과 `RefreshResponse` 객체 반환\n" +
                    "  - `accessToken` (String): 새로운 액세스 토큰\n" +
                    "  - `refreshToken` (String): 새로운 리프레시 토큰\n" +
                    "  - `expiresAt` (LocalDateTime): 새 토큰 만료 시각\n" +
                    "  - 성공 시 200(OK)과 새 토큰/만료시각 반환."
    )
    @Parameters({
            @Parameter(name = "Authorization", description = "Access Token (만료 허용). 형식: `Bearer <token>`", required = true,
                    in = ParameterIn.HEADER, schema = @Schema(type = "string")),
            @Parameter(name = "RefreshToken", description = "Refresh Token", required = true,
                    in = ParameterIn.HEADER, schema = @Schema(type = "string"))
    })
    @PostMapping("/tokens/refresh")
    public BaseResponse<RefreshResponse> refreshToken(
            @RequestHeader("RefreshToken") String refreshToken
    ) {
        return BaseResponse.onSuccess(SuccessStatus._OK, loginService.refresh(refreshToken));
    }


    // 로그아웃
    @Operation(
            summary = "로그아웃 API",
            description = "# [v1.0 (2025-09-03)](https://clumsy-seeder-416.notion.site/23a1197c19ed809e9a09fcd741f554c8?source=copy_link)\n" +
                    "- 헤더로 호출합니다.\n" +
                    "- 헤더: `Authorization: Bearer <accessToken>`.\n" +
                    "- 처리: Refresh 무효화(선택), Access 블랙리스트 등록.\n" +
                    "- 성공 시 200(OK)."
    )
    @DeleteMapping("/logout")
    public BaseResponse<Void> logout(
            @RequestHeader("Authorization")
            @Parameter(name = "Authorization", description = "Access Token. 형식: `Bearer <token>`", required = true,
                            in = ParameterIn.HEADER, schema = @Schema(type = "string"))
            String authorization
    ) {
        logoutService.logout(authorization);
        return BaseResponse.onSuccess(SuccessStatus._OK, null);
    }

    // 숭실대 인증 및 개인정보 조회
    @Operation(
            summary = "숭실대 유세인트 인증 API",
            description = "# [v1.0 (2025-09-03)](https://clumsy-seeder-416.notion.site/23a1197c19ed808d9266e641e5c4ea14?source=copy_link)\n" +
                    "- `application/json`으로 호출합니다.\n" +
                    "- 요청 바디: `USaintAuthRequest(sToken, sIdno)`.\n" +
                    "- 처리 순서:\n" +
                    "  1) 유세인트 SSO 로그인 시도 (sToken, sIdno 검증)\n" +
                    "  2) 응답 Body 검증 후 세션 쿠키 추출\n" +
                    "  3) 유세인트 포털 페이지 접근 및 HTML 파싱\n" +
                    "  4) 이름, 학번, 소속, 학적 상태, 학년/학기 정보 추출\n" +
                    "  5) 소속 문자열을 전공 Enum(`Major`)으로 매핑\n" +
                    "  6) 인증 결과를 `USaintAuthResponse` DTO로 반환\n"
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(schema = @Schema(implementation = USaintAuthRequest.class))
    )
    @PostMapping(value = "/students/ssu-verify", consumes = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<USaintAuthResponse> ssuAuth(
            @RequestBody @Valid USaintAuthRequest request
    ) {
        return BaseResponse.onSuccess(SuccessStatus._OK, ssuAuthService.uSaintAuth(request));
    }

}