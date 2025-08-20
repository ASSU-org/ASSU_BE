package com.assu.server.domain.member.entity;

import com.assu.server.domain.admin.entity.Admin;
import com.assu.server.domain.auth.entity.CommonAuth;
import com.assu.server.domain.auth.entity.SSUAuth;
import com.assu.server.domain.common.entity.BaseEntity;
import com.assu.server.domain.common.enums.ActivationStatus;
import com.assu.server.domain.common.enums.UserRole;
import com.assu.server.domain.partner.entity.Partner;
import com.assu.server.domain.user.entity.Student;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;


@Getter
@Setter
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String phoneNum;

    private Boolean isPhoneVerified;

    private LocalDateTime phoneVerifiedAt;

    private String profileUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UserRole role;  // STUDENT, ADMIN, PARTNER

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivationStatus isActivated;  // ACTIVE, INACTIVE, SUSPEND

    // 역할별 프로필 - 선택적으로 연관
    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL)
    private Student studentProfile;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL)
    private Admin adminProfile;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL)
    private Partner partnerProfile;

    // 스키마가 BIGINT라서 Long 사용 (필요 시 VARCHAR로 변경)
    @Column(name = "fcm_token")
    private Long fcmToken;

    // 연관관계 (1:1) — 양방향 필요 없으면 아래 필드 제거해도 됨
    @OneToOne(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private SSUAuth ssuAuth;

    @OneToOne(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private CommonAuth commonAuth;
}
