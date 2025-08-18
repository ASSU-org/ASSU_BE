package com.assu.server.domain.member.repository;

import java.util.Optional;

import com.assu.server.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByPhoneNum(String phoneNum);

    Optional<Member> findMemberById(Long id);
}
