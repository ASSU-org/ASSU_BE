package com.assu.server.domain.auth.security.common;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class CommonUsernamePasswordAuthenticationToken extends UsernamePasswordAuthenticationToken {
    public CommonUsernamePasswordAuthenticationToken(String email, String password) {
        super(email, password);
    }
}
