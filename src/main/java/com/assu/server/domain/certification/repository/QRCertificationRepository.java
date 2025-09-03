package com.assu.server.domain.certification.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.assu.server.domain.certification.entity.QRCertification;

public interface QRCertificationRepository extends JpaRepository<QRCertification, Long> {
}
