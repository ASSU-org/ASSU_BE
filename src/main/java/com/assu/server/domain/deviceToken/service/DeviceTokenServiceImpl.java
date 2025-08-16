package com.assu.server.domain.deviceToken.service;

import com.assu.server.domain.common.entity.Member;
import com.assu.server.domain.common.repository.MemberRepository;
import com.assu.server.domain.deviceToken.entity.DeviceToken;
import com.assu.server.domain.deviceToken.repository.DeviceTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeviceTokenServiceImpl implements DeviceTokenService {
    private final DeviceTokenRepository deviceTokenRepository;
    private final MemberRepository memberRepository;

    @Transactional
    @Override
    public void register(String tokenId, Long memberId) {
        Member member = memberRepository.findMemberById(memberId);

        DeviceToken dt = deviceTokenRepository.findByToken(tokenId)
                .map(deviceToken -> { deviceToken.setActive(true); return deviceToken; })
                .orElse(DeviceToken.builder().member(member).token(tokenId).active(true).build());
        deviceTokenRepository.save(dt);
    }

    @Override
    @Transactional
    public void unregister(Long tokenId) {
        deviceTokenRepository.findById(tokenId).ifPresent(
                deviceToken -> deviceToken.setActive(false)
        );
    }
}