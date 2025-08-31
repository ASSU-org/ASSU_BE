package com.assu.server.domain.mapping.repository;

import com.assu.server.domain.mapping.entity.StudentAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Collection;
import java.util.List;

public interface StudentAdminRepository extends JpaRepository<StudentAdmin, Long> {
    @Query("""
           select count(sa)
           from StudentAdmin sa
           where sa.admin.id = :adminId
           """)
    Long countAllByAdminId(@Param("adminId") Long adminId);


    @Query("""
           select count(sa)
           from StudentAdmin sa
           where sa.admin.id = :adminId
             and sa.createdAt >= :from
             and sa.createdAt <  :to
           """)
    Long countByAdminIdBetween(@Param("adminId") Long adminId,
                               @Param("from") LocalDateTime from,
                               @Param("to")   LocalDateTime to);

    default Long countThisMonthByAdminId(Long adminId) {
        LocalDateTime from = YearMonth.now().atDay(1).atStartOfDay();
        LocalDateTime to   = LocalDateTime.now();
        return countByAdminIdBetween(adminId, from, to);
    }
    // 오늘 하루, '나를 admin으로 제휴 맺은 partner'의 제휴를 사용한 '고유 사용자 수'
    @Query(value = """
        SELECT COUNT(DISTINCT pu.student_id)
        FROM partnership_usage pu
        JOIN paper_content pc ON pc.id = pu.paper_id
        JOIN paper p    ON p.id = pc.paper_id
        WHERE p.admin_id = :adminId
          AND pu.created_at >= CURRENT_DATE
          AND pu.created_at <  CURRENT_DATE + INTERVAL 1 DAY
        """, nativeQuery = true)
    Long countTodayUsersByAdmin(@Param("adminId") Long adminId);

    // 누적: admin이 제휴한 모든 store의 사용 건수 (0건 포함), 사용량 내림차순
    @Query(value = """
        SELECT
          p.store_id                        AS storeId,
          s.name                            AS storeName,
          CAST(COALESCE(COUNT(pu.id), 0) AS UNSIGNED) AS usageCount
        FROM paper p
        JOIN store s              ON s.id = p.store_id
        LEFT JOIN paper_content pc ON pc.paper_id = p.id
        LEFT JOIN partnership_usage pu ON pu.paper_id = pc.id
        WHERE p.admin_id = :adminId
        GROUP BY p.store_id, s.name
        ORDER BY usageCount DESC, storeId ASC
        """, nativeQuery = true)
    List<StoreUsage> findUsageByStore(@Param("adminId") Long adminId);

    interface StoreUsage {
        Long getStoreId();
        String getStoreName();
        Long getUsageCount();
    }
}
