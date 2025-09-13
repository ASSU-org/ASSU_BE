package com.assu.server.domain.auth.service;

import com.assu.server.domain.auth.security.jwt.JwtUtil;
import com.assu.server.domain.member.entity.Member;
import com.assu.server.domain.member.repository.MemberRepository;
import com.assu.server.domain.auth.exception.CustomAuthException;
import com.assu.server.global.apiPayload.code.status.ErrorStatus;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WithdrawalServiceImpl implements WithdrawalService {

    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public void withdrawCurrentUser(String authorization) {
        String rawAccessToken = jwtUtil.getTokenFromHeader(authorization);

        // Access 토큰에서 memberId 추출
        Claims claims = jwtUtil.validateTokenOnlySignature(rawAccessToken);
        Long memberId = ((Number) claims.get("userId")).longValue();

        log.info("현재 사용자 탈퇴 시작: memberId={}", memberId);

        // 2) 회원 탈퇴 처리
        withdrawMember(memberId);

        // 3) 현재 Access 토큰을 블랙리스트에 등록
        jwtUtil.blacklistAccess(rawAccessToken);

        log.info("현재 사용자 탈퇴 완료: memberId={}", memberId);
    }

    private void withdrawMember(Long memberId) {
        log.info("회원 탈퇴 시작: memberId={}", memberId);

        // 1) 회원 존재 여부 확인
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomAuthException(ErrorStatus.NO_SUCH_MEMBER));

        // 2) 이미 탈퇴된 회원인지 확인
        if (member.getDeletedAt() != null) {
            throw new CustomAuthException(ErrorStatus.MEMBER_ALREADY_WITHDRAWN);
        }

        // 3) 소프트 삭제: deletedAt 필드에 현재 시간 설정
        member.setDeletedAt(java.time.LocalDateTime.now());
        memberRepository.save(member);

        // 4) 해당 회원의 모든 토큰 무효화
        jwtUtil.removeAllRefreshTokens(memberId);

        log.info("회원 탈퇴 완료: memberId={}", memberId);
    }
}
