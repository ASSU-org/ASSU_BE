package com.assu.server.domain.auth.dto.signup;

import com.assu.server.domain.auth.dto.signup.common.CommonAuthPayload;
import com.assu.server.domain.auth.dto.signup.common.CommonInfoPayload;
import com.assu.server.domain.auth.dto.signup.common.CommonSignUpRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

/** 관리자 가입: multipart payload(JSON) */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AdminSignUpRequest extends CommonSignUpRequest {

    @Valid
    @NotNull
    private CommonAuthPayload commonAuth;

    @Valid
    @NotNull
    private CommonInfoPayload commonInfo;
    // signImage는 @RequestPart MultipartFile 로 별도 수신
}
