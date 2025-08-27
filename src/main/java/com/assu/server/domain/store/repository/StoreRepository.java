package com.assu.server.domain.store.repository;

import com.assu.server.domain.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {

    @Query("""
        SELECT s FROM Store s
        WHERE s.address = :address
          AND ((:detail IS NULL AND s.detailAddress IS NULL) OR s.detailAddress = :detail)
    """)

    Optional<Store> findBySameAddress(
            @Param("address") String address,
            @Param("detail") String detail
    );

    @Query(value = """
        SELECT s.*
        FROM store s
        WHERE s.point IS NOT NULL
          AND ST_Contains(ST_GeomFromText(:wkt, 4326), s.point)
        """, nativeQuery = true)
    List<Store> findAllWithinViewport(@Param("wkt") String wkt);

    List<Store> findByNameContainingIgnoreCaseOrderByIdDesc(String name);
}
