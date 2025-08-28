package com.assu.server.domain.partnership.repository;

import com.assu.server.domain.partnership.entity.Paper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface PartnershipRepository extends JpaRepository <Paper, Long> {

    Optional<Paper> findFirstByAdmin_IdAndStore_IdOrderByIdAsc(Long adminId, Long storeId);
}
