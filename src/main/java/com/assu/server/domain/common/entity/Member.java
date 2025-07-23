package com.assu.server.domain.common.entity;

import com.assu.server.domain.common.enums.ActivationStatus;
import com.assu.server.domain.common.enums.UserRole;
import com.assu.server.domain.admin.entity.Admin;
import com.assu.server.domain.partner.entity.Partner;
import com.assu.server.domain.user.entity.Student;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String phoneNum;

    private Boolean isPhoneVerified;

    private LocalDateTime phoneVerifiedAt;

    @Enumerated(EnumType.STRING)
    private UserRole role;  // User, ADMIN, PARTNER

    @Enumerated(EnumType.STRING)
    private ActivationStatus isActivated;  // ACTIVE, INACTIVE, SUSPEND

    // 역할별 프로필 - 선택적으로 연관
    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL)
    private Student studentProfile;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL)
    private Admin adminProfile;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL)
    private Partner partnerProfile;

    // 편의 메서드 및 Builder 등 생략
}

