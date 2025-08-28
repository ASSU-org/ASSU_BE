package com.assu.server.domain.auth.dto.ssu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class USaintAuthRequest {
    @NotNull
    private String sToken;
    @NotNull
    private Integer sIdno;
}
