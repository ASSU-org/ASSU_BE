package com.assu.server.domain.store.repository;

import java.util.Optional;

import org.hibernate.validator.internal.engine.resolver.JPATraversableResolver;
import org.springframework.data.jpa.repository.JpaRepository;

import com.assu.server.domain.store.entity.Store;

public interface StoreRepository extends JpaRepository<Store, Long> {
	Optional<Store> findByName(String name);
	Optional<Store> findById(Long id);

}
