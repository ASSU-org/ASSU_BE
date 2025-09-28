package com.assu.server.domain.chat.dto;

import lombok.Getter;
import lombok.Setter;

public class BlockRequestDTO {

    @Getter
    @Setter
    public static class BlockMemberRequestDTO {
        private Long opponentId;
    }
}
