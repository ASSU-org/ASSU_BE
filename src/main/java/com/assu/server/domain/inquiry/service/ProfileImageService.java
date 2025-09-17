package com.assu.server.domain.inquiry.service;

import org.springframework.web.multipart.MultipartFile;

public interface ProfileImageService {
    String updateProfileImage(Long memberId, MultipartFile image);
    String getProfileImageUrl(Long memberId);
}
