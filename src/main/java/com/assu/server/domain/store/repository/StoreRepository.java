package com.assu.server.domain.store.repository;

import com.assu.server.domain.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoreRepository extends JpaRepository<Store, Long> {
    List<Store> findByNameContainingIgnoreCaseOrderByIdDesc(String name);
}
