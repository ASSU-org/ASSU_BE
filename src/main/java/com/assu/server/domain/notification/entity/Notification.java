package com.assu.server.domain.notification.entity;
import com.assu.server.domain.certification.entity.QRCertification;
import com.assu.server.domain.common.entity.BaseEntity;
import com.assu.server.domain.partner.entity.Partner;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Notification extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "qr_id")
	private QRCertification qrVerification;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "partner_id")
	private Partner partner;

	private String content;
	private Boolean isChecked;
}