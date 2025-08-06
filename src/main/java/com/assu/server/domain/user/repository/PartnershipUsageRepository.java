package com.assu.server.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.assu.server.domain.user.entity.PartnershipUsage;

public interface PartnershipUsageRepository extends JpaRepository<PartnershipUsage, Long> {

}
