package com.assu.server.domain.user.entity;


import com.assu.server.domain.member.entity.Member;
import com.assu.server.domain.user.entity.enums.Department;
import com.assu.server.domain.user.entity.enums.EnrollmentStatus;
import com.assu.server.domain.user.entity.enums.Major;
import com.assu.server.domain.user.entity.enums.University;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
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

    private Department department;

    @Enumerated(EnumType.STRING)
    private EnrollmentStatus enrollmentStatus;

    @Pattern(regexp = "^[0-9]{1}-[1-2]$", message = "yearSemester는 Y-N 형식이어야 합니다. 예: 3-1")
    private String yearSemester;

    private University university;

    private int stamp;

    @Enumerated(EnumType.STRING)
    private Major major;

    public void setMember(Member member) {
        this.member = member;
    }

    public void setStamp() {
        if(this.stamp ==10)
            this.stamp=1;
        else
            this.stamp++;
    }
}
