package com.assu.server.domain.auth.dto.signup;

import com.assu.server.domain.auth.dto.common.UserBasicInfo;
import com.assu.server.domain.common.enums.ActivationStatus;
import com.assu.server.domain.common.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "회원가입 성공 응답")
public class SignUpResponse {

    @Schema(description = "회원 ID", example = "123")
    private Long memberId;

    @Schema(description = "회원 역할", example = "STUDENT")
    private UserRole role;

    @Schema(description = "회원 상태", example = "ACTIVE")
    private ActivationStatus status;

    @Schema(description = "액세스 토큰/리프레시 토큰")
    private Tokens tokens;

    @Schema(description = "사용자 기본 정보 (캐싱용)")
    private UserBasicInfo basicInfo;
}
