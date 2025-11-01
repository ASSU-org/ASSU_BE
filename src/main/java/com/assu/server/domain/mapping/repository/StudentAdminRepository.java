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

    // 총 누적 가입자 수
    @Query("""
           select count(sa)
           from StudentAdmin sa
           where sa.admin.id = :adminId
           """)
    Long countAllByAdminId(@Param("adminId") Long adminId);

    // 기간별 가입자 수
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

    // 이번 달 신규 가입자 수
    default Long countThisMonthByAdminId(Long adminId) {
        LocalDateTime from = YearMonth.now().atDay(1).atStartOfDay();
        LocalDateTime to   = LocalDateTime.now();
        return countByAdminIdBetween(adminId, from, to);
    }

    // 오늘 제휴 사용 고유 사용자 수
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

    @Query(value = """
        SELECT
          p.id                              AS paperId,
          p.store_id                        AS storeId,
          s.name                            AS storeName,
          CAST(COUNT(pu.id) AS UNSIGNED)    AS usageCount
        FROM paper p
        JOIN store s              ON s.id = p.store_id
        JOIN paper_content pc     ON pc.paper_id = p.id
        JOIN partnership_usage pu ON pu.paper_id = pc.id
        WHERE p.admin_id = :adminId
        GROUP BY p.id, p.store_id, s.name
        HAVING usageCount > 0
        ORDER BY usageCount DESC, p.id ASC
        """, nativeQuery = true)
    List<StoreUsageWithPaper> findUsageByStoreWithPaper(@Param("adminId") Long adminId);

    // 0건 포함 조회 (대시보드에서 모든 제휴 업체를 보여줘야 하는 경우)
    @Query(value = """
        SELECT
          p.id                                   AS paperId,
          p.store_id                             AS storeId,
          s.name                                 AS storeName,
          CAST(COALESCE(COUNT(pu.id), 0) AS UNSIGNED) AS usageCount
        FROM paper p
        JOIN store s              ON s.id = p.store_id
        LEFT JOIN paper_content pc ON pc.paper_id = p.id
        LEFT JOIN partnership_usage pu ON pu.paper_id = pc.id
        WHERE p.admin_id = :adminId
        GROUP BY p.id, p.store_id, s.name
        ORDER BY usageCount DESC, p.id ASC
        """, nativeQuery = true)
    List<StoreUsageWithPaper> findUsageByStoreIncludingZero(@Param("adminId") Long adminId);

    interface StoreUsageWithPaper {
        Long getPaperId();    // 🆕 추가: Paper ID
        Long getStoreId();
        String getStoreName();
        Long getUsageCount();
    }
}