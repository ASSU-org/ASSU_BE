package com.assu.server.domain.auth.dto.signup;

import com.assu.server.domain.auth.dto.signup.common.CommonSignUpRequest;
import com.assu.server.domain.auth.dto.signup.student.StudentAuthPayload;
import com.assu.server.domain.auth.dto.signup.student.StudentInfoPayload;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

/** 학생 가입: JSON */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class StudentSignUpRequest extends CommonSignUpRequest {

    @Valid
    @NotNull
    private StudentAuthPayload studentAuth;

    @Valid
    @NotNull
    private StudentInfoPayload studentInfo;
}
