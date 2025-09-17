package com.assu.server.domain.partnership.repository;

import com.assu.server.domain.partnership.entity.PaperContent;
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
          AND p.isActivated = com.assu.server.domain.paper.enums.IsActivated.ACTIVE
          AND CURRENT_DATE BETWEEN p.partnershipPeriodStart AND p.partnershipPeriodEnd
          AND (
                (pc.optionType = com.assu.server.domain.paper.enums.OptionType.SERVICE AND
                    (
                      (pc.criterionType = com.assu.server.domain.paper.enums.CriterionType.PRICE AND pc.cost IS NOT NULL)
                      OR
                      (pc.criterionType = com.assu.server.domain.paper.enums.CriterionType.HEADCOUNT AND pc.cost IS NOT NULL AND pc.people IS NOT NULL)
                    )
                )
                OR
                (pc.optionType = com.assu.server.domain.paper.enums.OptionType.DISCOUNT AND pc.discount IS NOT NULL)
              )
        ORDER BY pc.id DESC
        """)
    Optional<PaperContent> findLatestValidByStoreId(@Param("storeId") Long storeId);
}
