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
}
