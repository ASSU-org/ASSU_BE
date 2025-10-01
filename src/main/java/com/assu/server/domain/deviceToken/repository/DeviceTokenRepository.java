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
    @Query("select dt.token from DeviceToken dt where dt.member.id =:memberId and dt.active=true")
    List<String> findActiveTokensByMemberId(@Param("memberId") Long memberId);

    @Transactional
    @Modifying
    @Query("update DeviceToken dt set dt.active = false where dt.token in :tokens")
    void deactivateTokens(@Param("tokens") List<String> tokens);

    Optional<DeviceToken> findByToken(String token);
}
