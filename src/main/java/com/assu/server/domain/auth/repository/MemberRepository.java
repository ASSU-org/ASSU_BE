package com.assu.server.domain.auth.repository;

import com.assu.server.domain.auth.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByPhoneNum(String phoneNum);
    Member findMemberById(Long id);

}
