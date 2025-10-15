package com.assu.server.domain.deviceToken.repository;

import com.assu.server.domain.deviceToken.entity.DeviceToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface DeviceTokenRepository extends JpaRepository<DeviceToken, Long> {
    // (token, member) 기준으로 단건 조회 — register()에서 Upsert 용
    @Query("select dt from DeviceToken dt where dt.token = :token and dt.member.id = :memberId")
    Optional<DeviceToken> findByTokenAndMemberId(@Param("token") String token, @Param("memberId") Long memberId);

    // 멤버의 모든 활성 토큰 조회 — 푸시 발송용
    @Query("select dt.token from DeviceToken dt where dt.member.id = :memberId and dt.active = true")
    List<String> findActiveTokensByMemberId(@Param("memberId") Long memberId);

    // 토큰 ID(PK) 기준 조회 — unregister()에서 사용
    Optional<DeviceToken> findById(Long id);
}
