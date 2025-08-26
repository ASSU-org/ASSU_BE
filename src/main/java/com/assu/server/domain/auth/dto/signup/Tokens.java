package com.assu.server.domain.auth.dto.signup;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Tokens {
    private String accessToken;
    private String refreshToken;
}
