package com.assu.server.domain.auth.dto.ssu;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class USaintAuthRequest {
    @NotNull
    @JsonProperty(value = "sToken")
    private String sToken;
    @NotNull
    @JsonProperty(value = "sIdno")
    private String sIdno;
}
