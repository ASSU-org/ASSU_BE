package com.assu.server.domain.partnership.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.assu.server.domain.common.enums.ActivationStatus;
import com.assu.server.domain.partnership.entity.Paper;

public interface PaperRepository extends JpaRepository<Paper, Long> {

	@Query("SELECT p FROM Paper p " +
		"WHERE p.store.id = :storeId " +
		"AND p.admin.id = :adminId " +
		"AND p.isActivated = :status")
	List<Paper> findByStoreIdAndAdminIdAndStatus(
		@Param("storeId")Long storeId,
		@Param("adminId")Long adminId,
		@Param("status")ActivationStatus status);
}
