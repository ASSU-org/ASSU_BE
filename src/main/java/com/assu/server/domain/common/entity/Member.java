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
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String phoneNum;

    private Boolean isPhoneVerified;

    private LocalDateTime phoneVerifiedAt;

    private String profileUrl;

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

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public void setIsPhoneVerified(Boolean isPhoneVerified) {
        this.isPhoneVerified = isPhoneVerified;
    }

    public void setPhoneVerifiedAt(LocalDateTime phoneVerifiedAt) {
        this.phoneVerifiedAt = phoneVerifiedAt;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public void setIsActivated(ActivationStatus isActivated) {
        this.isActivated = isActivated;
    }


    // 하드코딩시에만 사용 -> 원격에 올리기 전 주석 처리
    public void setId(Long id){
        this.id = id;
    }

    // 연관관계 편의 메서드

    // public void setStudentProfile(Student studentProfile) {
    //     this.studentProfile = studentProfile;
    //     if (studentProfile.getMember() != this) {
    //         studentProfile.setMember(this);
    //     }
    // }
    //
    // public void setAdminProfile(Admin adminProfile) {
    //     this.adminProfile = adminProfile;
    //     if (adminProfile.getMember() != this) {
    //         adminProfile.setMember(this);
    //     }
    // }
    //
    // public void setPartnerProfile(Partner partnerProfile) {
    //     this.partnerProfile = partnerProfile;
    //     if (partnerProfile.getMember() != this) {
    //         partnerProfile.setMember(this);
    //     }
    // }


}

