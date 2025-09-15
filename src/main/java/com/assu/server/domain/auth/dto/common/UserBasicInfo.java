package com.assu.server.domain.auth.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "사용자 기본 정보")
public class UserBasicInfo {

    @Schema(description = "이름/업체명/단체명", example = "홍길동")
    private String name;

    @Schema(description = "대학교", example = "숭실대학교")
    private String university;

    @Schema(description = "단과대", example = "IT공과대학")
    private String department;

    @Schema(description = "전공/학과", example = "소프트웨어학부")
    private String major;
}
