package com.assu.server.domain.partnership.repository;

import com.assu.server.domain.common.enums.ActivationStatus;
import com.assu.server.domain.partnership.entity.PaperContent;
import com.assu.server.domain.partnership.entity.enums.CriterionType;
import com.assu.server.domain.partnership.entity.enums.OptionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PaperContentRepository extends JpaRepository<PaperContent, Long> {

    Optional<PaperContent> findTopByPaperStoreIdOrderByIdDesc(Long storeId);

	List<PaperContent> findByPaperId(Long paperId);

    @Query("""
           select distinct pc
           from PaperContent pc
           left join fetch pc.goods g
           where pc.paper.id in :paperIds
           """)
    List<PaperContent> findAllByPaperIdInFetchGoods(@Param("paperIds") List<Long> paperIds);

    @Query("""
           select distinct pc
           from PaperContent pc
           left join fetch pc.goods g
           where pc.paper.id in :paperIds
           """)
    List<PaperContent> findAllByOnePaperIdInFetchGoods(@Param("paperIds") Long paperIds);

    Optional<PaperContent> findById(Long id);

    @Query("""
        SELECT pc FROM PaperContent pc
        JOIN pc.paper p
        WHERE p.store.id = :storeId
          AND p.isActivated = :active
          AND (
                (p.partnershipPeriodStart IS NULL OR p.partnershipPeriodEnd IS NULL)
                OR
                (CURRENT_DATE BETWEEN p.partnershipPeriodStart AND p.partnershipPeriodEnd)
              )
          AND (
                (pc.optionType = :service AND
                    (
                      (pc.criterionType = :price AND pc.cost IS NOT NULL)
                      OR
                      (pc.criterionType = :headcount AND pc.cost IS NOT NULL AND pc.people IS NOT NULL)
                    )
                )
                OR
                (pc.optionType = :discount AND pc.discount IS NOT NULL)
              )
        ORDER BY pc.id DESC
        """)
    Optional<PaperContent> findLatestValidByStoreId(
            @Param("storeId") Long storeId,
            @Param("active") ActivationStatus active,
            @Param("service") OptionType service,
            @Param("discount") OptionType discount,
            @Param("price") CriterionType price,
            @Param("headcount") CriterionType headcount
    );
}
