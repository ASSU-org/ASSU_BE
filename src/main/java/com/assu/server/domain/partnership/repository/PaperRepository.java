package com.assu.server.domain.partnership.repository;

import com.assu.server.domain.partnership.entity.Paper;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaperRepository extends JpaRepository<Paper, Long> {
}
