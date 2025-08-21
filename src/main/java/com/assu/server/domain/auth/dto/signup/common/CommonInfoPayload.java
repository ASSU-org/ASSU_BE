package com.assu.server.domain.auth.dto.signup.common;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommonInfoPayload {
    @Size(min = 1, max = 50) @NotBlank
    private String name;

    @Size(min = 1, max = 255) @NotBlank
    private String address;

    @Size(max = 255)
    private String detailAddress;
}
