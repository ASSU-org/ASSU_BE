package com.assu.server.domain.store.entity;
import com.assu.server.domain.common.entity.BaseEntity;
import com.assu.server.domain.common.enums.ActivationStatus;
import com.assu.server.domain.partner.entity.Partner;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.locationtech.jts.geom.Point;


@Entity
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Store extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "partner_id")
	private Partner partner;

	private Integer rate;

	@Setter
	@Enumerated(EnumType.STRING)
	private ActivationStatus isActivate;

	@Setter
	private String name;

	private String address;

	private String detailAddress;

	@JdbcTypeCode(SqlTypes.GEOMETRY)
	private Point point;

	private double latitude;
	private double longitude;

	public void linkPartner(Partner partner) {
		this.partner = partner;
	}
	public void setGeo(Double lat, Double lng, Point point) {
		this.latitude = lat;
		this.longitude = lng;
		this.point = point;
	}

}