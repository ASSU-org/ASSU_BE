package com.assu.server.domain.auth.dto.signup.common;

import com.assu.server.domain.auth.exception.annotation.PasswordMatches;
import com.assu.server.domain.user.entity.enums.Department;
import com.assu.server.domain.user.entity.enums.Major;
import com.assu.server.domain.user.entity.enums.University;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommonAuthPayload {
    @Email @NotBlank
    private String email;

    @Size(min = 8, max = 72) @NotBlank
    private String password;

    private Department department;

    private Major major;

    private University university;
}
