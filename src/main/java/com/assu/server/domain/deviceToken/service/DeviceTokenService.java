package com.assu.server.domain.deviceToken.service;

import com.assu.server.domain.common.entity.Member;

public interface DeviceTokenService {
    void register(String token, Long memberId);
    void unregister(String token);
}
