package com.assu.server.domain.store.repository;

import com.assu.server.domain.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store,Long> {

}
