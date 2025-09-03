package com.assu.server.domain.deviceToken.service;

public interface DeviceTokenService {
    Long register(String tokenId, Long memberId);
    void unregister(Long tokenId, Long memberId);
}
