package com.assu.server.domain.deviceToken.service;

public interface DeviceTokenService {
    void register(String tokenId, Long memberId);
    void unregister(Long tokenId);
}
