package com.assu.server.domain.map.entity;

import com.assu.server.domain.common.entity.BaseEntity;
import com.assu.server.domain.map.entity.enums.LocationOwnerType;
import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;

@Entity
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Location extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Enumerated(EnumType.STRING)
    private LocationOwnerType ownerType;

    private Long ownerId;

    private String name;
    private String address;
    private String roadAddress;

    private Double latitude;
    private Double longitude;

    @Column(columnDefinition = "POINT SRID 4326")
    private Point point;
}
