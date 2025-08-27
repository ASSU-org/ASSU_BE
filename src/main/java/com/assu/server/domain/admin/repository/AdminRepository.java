package com.assu.server.domain.admin.repository;

import com.assu.server.domain.admin.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    // 후보 수 카운트: 해당 partner와 ACTIVE 제휴가 없는 admin 수
    @Query(value = """
        SELECT COUNT(*)
        FROM admin a
        LEFT JOIN paper pa
          ON pa.admin_id = a.id
         AND pa.partner_id = :partnerId
         AND pa.is_activated = 'ACTIVE'
        WHERE pa.id IS NULL
        """, nativeQuery = true)
    long countPartner(@Param("partnerId") Long partnerId);

    // 랜덤 오프셋으로 1~N건 가져오기 (LIMIT :offset, :limit)
    @Query(value = """
        SELECT a.*
        FROM admin a
        LEFT JOIN paper pa
          ON pa.admin_id = a.id
         AND pa.partner_id = :partnerId
         AND pa.is_activated = 'ACTIVE'
        WHERE pa.id IS NULL
        LIMIT :offset, :limit
        """, nativeQuery = true)
    List<Admin> findPartnerWithOffset(@Param("partnerId") Long partnerId,
                                                         @Param("offset") int offset,
                                                         @Param("limit") int limit);
}
