package com.assu.server.domain.admin.entity;

import com.assu.server.domain.common.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Admin {

    @Id
    private Long id;  // member_id와 동일

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private Member member;

    private String name;

    private String officeAddress;

    private String detailAddress;

    private String signUrl;

    private Boolean isSignVerified;

    private LocalDateTime signVerifiedAt;
}
