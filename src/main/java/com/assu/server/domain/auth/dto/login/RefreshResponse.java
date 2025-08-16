package com.assu.server.domain.auth.dto.login;

public record RefreshResponse(Long memberId, String newAccess, String newRefresh) {
}
