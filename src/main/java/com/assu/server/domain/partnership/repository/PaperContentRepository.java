package com.assu.server.domain.partnership.repository;

import com.assu.server.domain.partnership.entity.PaperContent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaperContentRepository extends JpaRepository<PaperContent, Long> {

    Optional<PaperContent> findTopByPaperStoreIdOrderByIdDesc(Long storeId);
}
