package com.assu.server.domain.partnership.repository;

import com.assu.server.domain.admin.entity.Admin;
import com.assu.server.domain.common.enums.ActivationStatus;
import com.assu.server.domain.partner.entity.Partner;
import com.assu.server.domain.partnership.entity.Paper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PaperRepository extends JpaRepository<Paper, Long> {

    Optional<Paper> findTopByAdmin_IdAndPartner_IdAndIsActivatedOrderByIdDesc(
            Long adminId, Long partnerId, ActivationStatus isActivated
    );

    Optional<Paper> findTopPaperByStoreId(Long storeId);

    // 로그인 admin과 활성 제휴 중이며 파트너 이름이 키워드와 매칭
    @Query("""
        select distinct p.partner
        from Paper p
        where p.admin.id = :adminId
          and p.isActivated = :status
          and lower(p.partner.name) like lower(concat('%', :keyword, '%'))
        order by p.id desc
    """)
    List<Partner> findActivePartnersForAdminByKeyword(@Param("adminId") Long adminId,
                                                      @Param("status") ActivationStatus status,
                                                      @Param("keyword") String keyword);

    // 로그인 partner와 활성 제휴 중이며 관리자 이름이 키워드와 매칭
    @Query("""
        select distinct p.admin
        from Paper p
        where p.partner.id = :partnerId
          and p.isActivated = :status
          and lower(p.admin.name) like lower(concat('%', :keyword, '%'))
        order by p.id desc
    """)
    List<Admin> findActiveAdminsForPartnerByKeyword(@Param("partnerId") Long partnerId,
                                                    @Param("status") ActivationStatus status,
                                                    @Param("keyword") String keyword);
}
