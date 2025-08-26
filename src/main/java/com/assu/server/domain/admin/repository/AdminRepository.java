package com.assu.server.domain.admin.repository;

import com.assu.server.domain.admin.entity.Admin;
import com.assu.server.domain.common.enums.ActivationStatus;
import com.assu.server.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    @Query(value = """
        SELECT a.*
        FROM admin a
        WHERE a.point IS NOT NULL
          AND ST_Contains(ST_GeomFromText(:wkt, 4326), a.point)
        """, nativeQuery = true)
    List<Admin> findAllWithinViewport(@Param("wkt") String wkt);

    @Query("""
        select distinct a
        from Admin a
        where lower(a.name) like lower(concat('%', :keyword, '%'))
          and exists (
              select 1 from Paper pc
              where pc.admin = a
                and pc.partner.id = :partnerId
                and pc.isActivated = :status
          )
        """)
    List<Admin> searchPartneredByName(
            @Param("partnerId") Long partnerId,
            @Param("status") ActivationStatus status,
            @Param("keyword") String keyword
    );

    Long member(Member member);
}
