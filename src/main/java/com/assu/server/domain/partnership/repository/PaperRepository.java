package com.assu.server.domain.partnership.repository;

import com.assu.server.domain.admin.entity.Admin;
import com.assu.server.domain.common.enums.ActivationStatus;
import com.assu.server.domain.partner.entity.Partner;
import com.assu.server.domain.partnership.entity.Paper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PaperRepository extends JpaRepository<Paper, Long> {

	@Query("SELECT p FROM Paper p " +
		"WHERE p.store.id = :storeId " +
		"AND p.admin.id = :adminId " +
		"AND p.isActivated = :status")
	List<Paper> findByStoreIdAndAdminIdAndStatus(
		@Param("storeId")Long storeId,
		@Param("adminId")Long adminId,
		@Param("status")ActivationStatus status);


    // Admin 기준 (ACTIVE)
    List<Paper> findByAdmin_IdAndIsActivated(Long adminId, ActivationStatus status, Sort sort);
    Page<Paper> findByAdmin_IdAndIsActivated(Long adminId, ActivationStatus status, Pageable pageable);
    Optional<Paper> findTopByAdmin_IdAndPartner_IdAndIsActivatedOrderByIdDesc(
            Long adminId, Long partnerId, ActivationStatus isActivated
    );

    boolean existsByAdmin_IdAndPartner_IdAndIsActivatedIn(Long adminId, Long partnerId, List<ActivationStatus> statuses);
    Optional<Paper> findTopByAdmin_IdAndPartner_IdAndIsActivatedInOrderByIdDesc(Long adminId, Long partnerId, List<ActivationStatus> statuses);

    // Admin 기준 (SUSPEND)
    @Query(" select p from Paper p left join fetch p.partner pt left join fetch p.store s where p.isActivated = :status and p.admin.id = :adminId and p.partner is null order by p.createdAt desc")
    List<Paper> findAllSuspendedByAdminWithNoPartner(
            @Param("status") ActivationStatus status,
            @Param("adminId") Long adminId
    );

    // Partner 기준 (ACTIVE)
    List<Paper> findByPartner_IdAndIsActivated(Long partnerId, ActivationStatus status, Sort sort);
    Page<Paper>  findByPartner_IdAndIsActivated(Long partnerId, ActivationStatus status, Pageable pageable);
    Optional<Paper> findTopPaperByStoreId(Long storeId);
    long countByStore_Id(Long storeId);

    @Query("""
        SELECT p FROM Paper p
        WHERE p.admin.id IN :adminIds
          AND p.isActivated = :status
          AND p.partnershipPeriodStart <= :today
          AND p.partnershipPeriodEnd >= :today
    """)
    List<Paper> findActivePapersByAdminIds(@Param("adminIds") List<Long> adminIds,
                                           @Param("today") LocalDate today,
                                           @Param("status") ActivationStatus status);

    List<Paper> findByStoreIdAndAdminIdAndIsActivated(Long storeId, Long adminId, ActivationStatus isActivated);
}
