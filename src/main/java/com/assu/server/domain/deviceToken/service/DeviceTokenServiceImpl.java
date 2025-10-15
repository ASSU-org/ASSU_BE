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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeviceTokenServiceImpl implements DeviceTokenService {

    private final DeviceTokenRepository deviceTokenRepository;
    private final MemberRepository memberRepository;

    /**
     * 동일 (token, member) 쌍이 존재하면 active=true 로 갱신,
     * 없으면 새로 생성하는 Upsert 로직
     */
    @Transactional
    @Override
    public Long register(String token, Long memberId) {
        Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NO_SUCH_MEMBER));

        try {
            // ① 이미 존재하는 (token, member)면 활성화만
            return deviceTokenRepository.findByTokenAndMemberId(token, memberId)
                    .map(existing -> {
                        existing.setActive(true);
                        return existing.getId();
                    })
                    // ② 없으면 새로 저장
                    .orElseGet(() -> {
                        DeviceToken newToken = DeviceToken.builder()
                                .member(member)
                                .token(token)
                                .active(true)
                                .build();
                        return deviceTokenRepository.save(newToken).getId();
                    });
        } catch (DataIntegrityViolationException e) {
            // ③ 동시성 충돌 시(복합 유니크 중복) 재조회 후 활성화
            return deviceTokenRepository.findByTokenAndMemberId(token, memberId)
                    .map(existing -> {
                        existing.setActive(true);
                        return existing.getId();
                    })
                    .orElseThrow(() -> e);
        }
    }

    /**
     * 해당 deviceTokenId 가 memberId 의 소유인지 검증 후 비활성화
     */
    @Transactional
    @Override
    public void unregister(Long deviceTokenId, Long memberId) {
        deviceTokenRepository.findById(deviceTokenId)
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