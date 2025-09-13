package com.assu.server.domain.user.entity.enums;

public enum Major {
    SW(Department.IT), // 소프트웨어학부
    GM(Department.IT), // 글로벌미디어학과
    COM(Department.IT), // 컴퓨터학부
    EE(Department.IT), // 전자정보공학부
    IP(Department.IT), // 정보보호학과
    AI(Department.IT), // AI융합학과
    MB(Department.IT); // 미디어경영학과

    private final Department department;

    Major(Department department) {
        this.department = department;
    }

    public Department getDepartment() {
        return department;
    }
}
