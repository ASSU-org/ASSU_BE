package com.assu.server.domain.inquiry.service;

import com.assu.server.domain.auth.exception.CustomAuthException;
import com.assu.server.domain.member.entity.Member;
import com.assu.server.domain.member.repository.MemberRepository;
import com.assu.server.global.apiPayload.code.status.ErrorStatus;
import com.assu.server.infra.s3.AmazonS3Manager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileImageServiceImpl implements ProfileImageService{

    private static final long MAX_SIZE_BYTES = 5 * 1024 * 1024; // 5MB
    private static final String[] ALLOWED_EXT = {"jpg", "jpeg", "png", "webp"};

    private final MemberRepository memberRepository;
    private final AmazonS3Manager amazonS3Manager;

    @Override
    @Transactional
    public String updateProfileImage(Long memberId, MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new CustomAuthException(ErrorStatus.PROFILE_IMAGE_NOT_FOUND);
        }

        // 1) 멤버 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomAuthException(ErrorStatus.NO_SUCH_MEMBER));

        // 2) 업로드 (generateKeyName + uploadFile 만 사용)
        String keyPath = "members/" + member.getId() + "/profile/" + image.getOriginalFilename();
        String keyName = amazonS3Manager.generateKeyName(keyPath);
        String uploadedKey = amazonS3Manager.uploadFile(keyName, image); // S3에 올린 후 key 반환

        // 3) 기존 파일 있으면 삭제 (기존 값이 key 라는 전제)
        String oldKey = member.getProfileUrl();
        if (oldKey != null && !oldKey.isBlank()) {
            try { amazonS3Manager.deleteFile(oldKey); }
            catch (Exception e) { log.warn("이전 프로필 삭제 실패 key={}", oldKey, e); }
        }

        // 4) DB 업데이트 (key 저장)
        member.setProfileUrl(uploadedKey);

        // 5) 호출자에 key 반환 (FE는 필요 시 presigned URL 생성해 사용)
        return uploadedKey;
    }
}

