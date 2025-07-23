package com.assu.server.domain.user.entity;

import com.assu.server.domain.common.entity.Member;
import com.assu.server.domain.user.entity.enums.EnrollmentStatus;
import com.assu.server.domain.user.entity.enums.Major;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Student {
    @Id
    private Long id;

    @OneToOne
    @JoinColumn(name = "id") // member_id와 공유
    @MapsId
    private Member member;

    private String department;

    @Enumerated(EnumType.STRING)
    private EnrollmentStatus enrollmentStatus;

    private String yearSemester;

    private String university;

    private int stamp;

    @Enumerated(EnumType.STRING)
    private Major major;
}
