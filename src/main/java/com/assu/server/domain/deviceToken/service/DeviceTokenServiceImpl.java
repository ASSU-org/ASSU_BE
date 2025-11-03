package com.assu.server.domain.deviceToken.service;

import com.assu.server.domain.deviceToken.entity.DeviceToken;
import com.assu.server.domain.deviceToken.repository.DeviceTokenRepository;
import com.assu.server.domain.member.entity.Member;
import com.assu.server.domain.member.repository.MemberRepository;
import com.assu.server.global.apiPayload.code.status.ErrorStatus;
import com.assu.server.global.exception.DatabaseException;
import com.assu.server.global.exception.GeneralException;

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
    public Long register(String tokenId, Long memberId) {
        Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NO_SUCH_MEMBER));

        // 1) 같은 회원 + 같은 토큰이 이미 있으면 → active = true 로만 복구
        //    (가장 정확한 쿼리: findByMemberIdAndToken)
        var sameTokenOpt = deviceTokenRepository.findByMemberIdAndToken(memberId, tokenId);
        if (sameTokenOpt.isPresent()) {
            DeviceToken exist = sameTokenOpt.get();
            exist.setActive(true);
            deviceTokenRepository.save(exist);
            return exist.getId();
        }

        // 2) 같은 회원 + 다른 토큰 → 그 회원의 기존 active 토큰 전부 비활성화
        //    (현재 보유 메서드 활용: 활성 토큰 문자열 가져와 deactivate)
        var activeTokens = deviceTokenRepository.findActiveTokensByMemberId(memberId);
        if (!activeTokens.isEmpty()) {
            // 현재 등록하려는 tokenId 와 다른 것들만 비활성화
            var toDeactivate = activeTokens.stream()
                    .filter(t -> !t.equals(tokenId))
                    .toList();
            if (!toDeactivate.isEmpty()) {
                deviceTokenRepository.deactivateTokens(toDeactivate);
            }
        }

        // 3) 새 토큰 insert (다른 회원이 같은 토큰을 갖고 있어도 상관 없이 insert)
        DeviceToken newToken = DeviceToken.builder()
                .member(member)
                .token(tokenId)
                .active(true)
                .build();
        deviceTokenRepository.save(newToken);

        return newToken.getId();
    }

    @Transactional
    @Override
    public void unregister(Long tokenId, Long memberId) {
        deviceTokenRepository.findById(tokenId)
                .ifPresentOrElse(deviceToken -> {
                    if (!deviceToken.getMember().getId().equals(memberId)) {
                        throw new DatabaseException(ErrorStatus.DEVICE_TOKEN_NOT_OWNED);
                    }
                    deviceToken.setActive(false);
                }, () -> {
                    throw new DatabaseException(ErrorStatus.DEVICE_TOKEN_NOT_FOUND);
                });
    }
}