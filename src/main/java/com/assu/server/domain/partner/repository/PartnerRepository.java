package com.assu.server.domain.partner.repository;

import com.assu.server.domain.partner.entity.Partner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartnerRepository extends JpaRepository<Partner, Long> {
}
