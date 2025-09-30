package com.assu.server.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class BlockResponseDTO {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BlockMemberDTO {
        private Long memberId;
        private String name;
        private LocalDateTime blockDate;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CheckBlockMemberDTO {
        private Long memberId;
        private String name;
        private boolean blocked;
    }

}
