package com.assu.server.domain.auth.schduler;

import com.assu.server.domain.member.entity.Member;
import com.assu.server.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberCleanupScheduler {

    private final MemberRepository memberRepository;

    @Scheduled(cron = "0 0 2 * * ?") // 매일 오전 2시
    @Transactional
    public void cleanupDeletedMembers() {
        log.info("탈퇴 회원 완전 삭제 작업 시작");

        // 한 달 전 시점 계산
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);

        // 한 달 이상 전에 탈퇴한 회원들 조회
        List<Member> membersToDelete = memberRepository.findByDeletedAtBefore(oneMonthAgo);

        if (membersToDelete.isEmpty()) {
            log.info("완전 삭제할 탈퇴 회원이 없습니다.");
            return;
        }

        log.info("완전 삭제할 탈퇴 회원 수: {}", membersToDelete.size());

        // 실제 데이터베이스에서 삭제
        memberRepository.deleteAll(membersToDelete);

        log.info("탈퇴 회원 완전 삭제 작업 완료: {}명 삭제됨", membersToDelete.size());
    }
}
