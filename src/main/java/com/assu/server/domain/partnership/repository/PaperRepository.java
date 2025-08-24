package com.assu.server.domain.partnership.repository;

import com.assu.server.domain.common.enums.ActivationStatus;
import com.assu.server.domain.partnership.entity.Paper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaperRepository extends JpaRepository<Paper, Long> {

    // Admin 기준 (ACTIVE)
    List<Paper> findByAdmin_IdAndIsActivated(Long adminId, ActivationStatus status, Sort sort);
    Page<Paper> findByAdmin_IdAndIsActivated(Long adminId, ActivationStatus status, Pageable pageable);

    // Partner 기준 (ACTIVE)
    List<Paper> findByPartner_IdAndIsActivated(Long partnerId, ActivationStatus status, Sort sort);
    Page<Paper>  findByPartner_IdAndIsActivated(Long partnerId, ActivationStatus status, Pageable pageable);
}
