package com.assu.server.domain.partnership.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.assu.server.domain.partnership.entity.PaperContent;

public interface PaperContentRepository extends JpaRepository<PaperContent, Long> {
	List<PaperContent> findByPaperId(Long paperId);
}
