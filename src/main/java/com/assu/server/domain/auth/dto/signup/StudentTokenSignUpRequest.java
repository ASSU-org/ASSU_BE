package com.assu.server.domain.auth.dto.signup;

import com.assu.server.domain.auth.dto.signup.common.CommonSignUpRequest;
import com.assu.server.domain.auth.dto.signup.student.StudentTokenAuthPayload;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

/** 학생 가입: sToken, sIdno 기반 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class StudentTokenSignUpRequest extends CommonSignUpRequest {

    @Valid
    @NotNull
    private StudentTokenAuthPayload studentTokenAuth;
}

