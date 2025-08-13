package com.assu.server.domain.mapping.repository;

import com.assu.server.domain.mapping.entity.StudentAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;

public interface StudentAdminRepository extends JpaRepository<StudentAdmin, Long> {
    @Query("""
           select count(sa)
           from StudentAdmin sa
           where sa.admin.id = :adminId
           """)
    Long countAllByAdminId(@Param("adminId") Long adminId);
}
