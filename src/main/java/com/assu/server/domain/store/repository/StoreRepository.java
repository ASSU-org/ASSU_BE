package com.assu.server.domain.store.repository;

import com.assu.server.domain.partner.entity.Partner;
import com.assu.server.domain.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store,Long> {
    Optional<Store> findByPartner(Partner  partner);
}
