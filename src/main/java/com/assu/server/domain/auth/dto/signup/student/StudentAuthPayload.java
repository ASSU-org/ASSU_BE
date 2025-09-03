package com.assu.server.domain.auth.dto.signup.student;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentAuthPayload {
    @Pattern(regexp = "^\\d{8,10}$", message = "학번은 숫자 8~10자리여야 합니다.")
    @NotBlank
    private String studentNumber;

    @Size(min = 4, max = 64, message = "비밀번호 길이가 올바르지 않습니다.")
    @NotBlank
    private String studentPassword; // 저장 전 대칭키 암호화 권장
}
