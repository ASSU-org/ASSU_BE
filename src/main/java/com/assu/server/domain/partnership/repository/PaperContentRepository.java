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
}
