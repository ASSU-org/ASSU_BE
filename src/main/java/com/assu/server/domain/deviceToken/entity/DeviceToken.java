package com.assu.server.domain.deviceToken.entity;

import com.assu.server.domain.auth.entity.Member;
import com.assu.server.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceToken extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="member_id", nullable=false)
    private Member member;

    @Column(nullable=false, length=200, unique=true)
    private String token;

    @Setter
    @Column(nullable=false)
    private boolean active;
}
