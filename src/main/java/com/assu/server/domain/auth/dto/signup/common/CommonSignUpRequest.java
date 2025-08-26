package com.assu.server.domain.auth.dto.signup.common;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/** 공통 필드 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CommonSignUpRequest {

    @Pattern(regexp = "^(01[016789])\\d{3,4}\\d{4}$", message = "휴대폰 번호 형식이 올바르지 않습니다.")
    @NotBlank
    private String phoneNumber;

    @NotNull
    private Boolean marketingAgree;

    @NotNull
    private Boolean locationAgree;
}
