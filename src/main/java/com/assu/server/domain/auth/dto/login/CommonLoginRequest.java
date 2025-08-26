package com.assu.server.domain.auth.dto.login;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/** 파트너/관리자 공통 로그인 요청 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "파트너/관리자 공통 로그인 요청")
public class CommonLoginRequest {

    @Schema(description = "로그인 이메일", example = "user@example.com")
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @Size(max = 255, message = "이메일은 255자를 넘을 수 없습니다.")
    private String email;

    @Schema(description = "로그인 비밀번호(평문)", example = "P@ssw0rd!")
    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, max = 64, message = "비밀번호는 8~64자여야 합니다.")
    private String password;
}
