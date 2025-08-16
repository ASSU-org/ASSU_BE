package com.assu.server.domain.auth.dto.signup;

import com.assu.server.domain.auth.dto.signup.common.CommonAuthPayload;
import com.assu.server.domain.auth.dto.signup.common.CommonInfoPayload;
import com.assu.server.domain.auth.dto.signup.common.CommonSignUpRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/** 제휴업체 가입: multipart payload(JSON) */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PartnerSignUpRequest extends CommonSignUpRequest {

    @Valid
    @NotNull
    private CommonAuthPayload commonAuth;

    @Valid
    @NotNull
    private CommonInfoPayload commonInfo;
    // licenseImage는 @RequestPart MultipartFile 로 별도 수신
}
