package com.assu.server.domain.auth.service;

public interface PhoneAuthService {
    void sendAuthNumber(String phoneNumber);
    void verifyAuthNumber(String phoneNumber, String authNumber);
}
