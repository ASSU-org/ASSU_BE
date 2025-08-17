package com.assu.server.domain.auth.security.student;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class StudentUsernamePasswordAuthenticationToken extends UsernamePasswordAuthenticationToken {
    public StudentUsernamePasswordAuthenticationToken(String studentNumber, String studentPassword) {
        super(studentNumber, studentPassword);
    }
}

