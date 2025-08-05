package com.assu.server.domain.review.repository;

import com.assu.server.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("""
    SELECT r
    FROM Review r
    WHERE r.student.id = :memberId
    ORDER BY r.createdAt DESC
""")
    List<Review> findByMemberId(@Param("memberId") Long memberId);
}
