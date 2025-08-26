package com.assu.server.domain.auth.dto.signup.student;

import com.assu.server.domain.user.entity.enums.Department;
import com.assu.server.domain.user.entity.enums.EnrollmentStatus;
import com.assu.server.domain.user.entity.enums.Major;
import com.assu.server.domain.user.entity.enums.University;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentInfoPayload {

    @NotNull
    private Department department;          // 단과대

    @NotNull
    private EnrollmentStatus enrollmentStatus; // 재학 상태: ENROLLED, LEAVE, GRADUATED

    @NotBlank
    @Pattern(regexp = "^[1-5]{1}-[1-2]$", message = "yearSemester는 Y-N 형식이어야 합니다. 예: 4-1")
    @Size(max = 10)
    private String yearSemester;        // 예: 2025-1

    @NotNull
    private University university;          // 학교명

    @NotBlank
    @Size(max = 50)
    private String major;               // 전공
}
