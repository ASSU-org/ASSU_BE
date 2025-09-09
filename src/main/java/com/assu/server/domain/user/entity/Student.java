package com.assu.server.domain.user.entity;

import com.assu.server.domain.member.entity.Member;
import com.assu.server.domain.user.entity.enums.Department;
import com.assu.server.domain.user.entity.enums.EnrollmentStatus;
import com.assu.server.domain.user.entity.enums.Major;
import com.assu.server.domain.user.entity.enums.University;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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

    private String name;

    @Enumerated(EnumType.STRING)
    private Department department;

    @Enumerated(EnumType.STRING)
    private EnrollmentStatus enrollmentStatus;

    private String yearSemester;

    @Enumerated(EnumType.STRING)
    private University university;

    private int stamp;

    @Enumerated(EnumType.STRING)
    private Major major;

    public void setMember(Member member) {
        this.member = member;
    }

    public void setStamp() {
        if (this.stamp == 10)
            this.stamp = 1;
        else
            this.stamp++;
    }

    /**
     * 유세인트에서 크롤링한 최신 정보로 학생 정보를 업데이트합니다.
     * 
     * @param name             학생 이름
     * @param major            전공
     * @param enrollmentStatus 학적 상태
     * @param yearSemester     학년/학기
     */
    public void updateStudentInfo(String name, Major major, EnrollmentStatus enrollmentStatus, String yearSemester) {
        this.name = name;
        this.major = major;
        this.enrollmentStatus = enrollmentStatus;
        this.yearSemester = yearSemester;
    }
}
