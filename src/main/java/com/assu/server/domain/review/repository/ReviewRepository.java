package com.assu.server.domain.review.repository;

import com.assu.server.domain.review.entity.Review;
import com.assu.server.domain.store.entity.Store;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    Page<Review> findByMemberId(@Param("memberId") Long memberId, Pageable pageable);
    // List<Review> findByStoreId(Long storeId);

    Page<Review> findByStoreIdOrderByCreatedAtDesc(Long id, Pageable pageable);//최신순 정렬
    Page<Review> findByStoreId(Long id, Pageable pageable);

    @Query("SELECT AVG(r.rate) FROM Review r WHERE r.store.id = :storeId")
    Float standardScore(Long storeId);

    Long store(Store store);
}
