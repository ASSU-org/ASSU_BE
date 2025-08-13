package com.assu.server.domain.mapping.repository;

import com.assu.server.domain.mapping.entity.StudentAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Collection;

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
}
