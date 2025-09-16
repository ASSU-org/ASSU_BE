package com.assu.server.domain.inquiry.dto.profileImage;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProfileImageResponse {
    @Schema(description = "업로드된 프로필 이미지 URL")
    private String url;
}
