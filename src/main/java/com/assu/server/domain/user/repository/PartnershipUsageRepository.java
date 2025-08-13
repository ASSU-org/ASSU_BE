package com.assu.server.domain.user.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.assu.server.domain.user.entity.PartnershipUsage;

public interface PartnershipUsageRepository extends JpaRepository<PartnershipUsage, Long> {

	@Query(value = """
        SELECT place
        FROM partnership_usage
        WHERE date >= CONVERT_TZ(CURDATE(), '+00:00', '+09:00')
          AND date <  CONVERT_TZ(CURDATE() + INTERVAL 1 DAY, '+00:00', '+09:00')
        GROUP BY place
        ORDER BY COUNT(*) DESC
        LIMIT 10
        """, nativeQuery = true)
	List<String> findTodayPopularPartnership();
}
