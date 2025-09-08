package com.assu.server.domain.auth.dto.signup.student;

import com.assu.server.domain.user.entity.enums.University;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentTokenAuthPayload {
    @Schema(description = "유세인트 sToken", example = "Vy3zFySFx5FASz175Kx7AzKyuSFQEgQ...")
    @NotNull(message = "sToken은 필수입니다.")
    @JsonProperty(value = "sToken")
    private String sToken;

    @Schema(description = "유세인트 sIdno", example = "20211438")
    @NotNull(message = "sIdno는 필수입니다.")
    @JsonProperty(value = "sIdno")
    private String sIdno;

    private University university;
}

