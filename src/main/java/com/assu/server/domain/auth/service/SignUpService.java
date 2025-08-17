package com.assu.server.domain.auth.service;

import com.assu.server.domain.auth.dto.signup.AdminSignUpRequest;
import com.assu.server.domain.auth.dto.signup.PartnerSignUpRequest;
import com.assu.server.domain.auth.dto.signup.SignUpResponse;
import com.assu.server.domain.auth.dto.signup.StudentSignUpRequest;
import org.springframework.web.multipart.MultipartFile;

public interface SignUpService {
    SignUpResponse signupStudent(StudentSignUpRequest req);
    SignUpResponse signupPartner(PartnerSignUpRequest req, MultipartFile licenseImage);
    SignUpResponse signupAdmin(AdminSignUpRequest req, MultipartFile signImage);
}
