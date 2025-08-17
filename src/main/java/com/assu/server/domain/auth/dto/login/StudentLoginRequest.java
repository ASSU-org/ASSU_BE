package com.assu.server.domain.auth.dto.login;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/** 학생 로그인 요청 (현재 서비스 로직이 이메일/비밀번호를 사용 중이면 LoginRequest를 그대로 사용해도 됩니다) */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "학생 로그인 요청")
public class StudentLoginRequest {

    @Schema(description = "학번", example = "student@example.com")
    @NotBlank(message = "학번은 필수입니다.")
    @Size(max = 10, message = "이메일은 10자를 넘을 수 없습니다.")
    private String studentNumber;

    @Schema(description = "로그인 비밀번호(평문)", example = "P@ssw0rd!")
    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, max = 64, message = "비밀번호는 8~64자여야 합니다.")
    private String studentPassword;
}

