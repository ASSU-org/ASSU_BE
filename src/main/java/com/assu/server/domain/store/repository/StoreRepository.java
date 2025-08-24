package com.assu.server.domain.store.repository;

import com.assu.server.domain.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {

    Optional<Store> findByNameAndAddressAndDetailAddress(String name, String address, String detailAddress);
}
