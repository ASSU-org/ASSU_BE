package com.assu.server.domain.partner.repository;

import com.assu.server.domain.common.enums.ActivationStatus;
import com.assu.server.domain.partner.entity.Partner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PartnerRepository extends JpaRepository<Partner,Long> {

    @Query(value = """
        SELECT p.*
        FROM partner p
        WHERE p.point IS NOT NULL
          AND ST_Contains(ST_GeomFromText(:wkt, 4326), p.point)
        """, nativeQuery = true)
    List<Partner> findAllWithinViewport(@Param("wkt") String wkt);

    @Query("""
        select distinct p
        from Partner p
        where lower(p.name) like lower(concat('%', :keyword, '%'))
          and exists (
              select 1 from Paper pc
              where pc.partner = p
                and pc.admin.id = :adminId
                and pc.isActivated = :status
          )
        """)
    List<Partner> searchPartneredByName(
            @Param("adminId") Long adminId,
            @Param("status") ActivationStatus status,
            @Param("keyword") String keyword
    );
}
