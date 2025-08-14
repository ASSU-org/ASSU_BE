package com.assu.server.domain.admin.entity;

import com.assu.server.domain.common.entity.Member;
import com.assu.server.domain.user.entity.enums.Major;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Setter
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

    @Enumerated(EnumType.STRING)
    private Major major;

    public void setMember(Member member) {
        this.member = member;
    }
}
