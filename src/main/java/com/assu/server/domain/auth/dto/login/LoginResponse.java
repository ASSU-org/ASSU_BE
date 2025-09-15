package com.assu.server.domain.auth.dto.login;

import com.assu.server.domain.auth.dto.common.UserBasicInfo;
import com.assu.server.domain.auth.dto.signup.Tokens;
import com.assu.server.domain.common.enums.ActivationStatus;
import com.assu.server.domain.common.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "로그인 성공 응답")
public class LoginResponse {

    @Schema(description = "회원 ID", example = "123")
    private Long memberId;

    @Schema(description = "회원 역할", example = "STUDENT")
    private UserRole role;

    @Schema(description = "회원 상태", example = "SUSPEND")
    private ActivationStatus status;

    @Schema(description = "액세스 토큰/리프레시 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private Tokens tokens;

    @Schema(description = "사용자 기본 정보 (캐싱용)")
    private UserBasicInfo basicInfo;
}
