package com.assu.server.domain.common.repository;

import java.util.Optional;

import com.assu.server.domain.common.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;



public interface MemberRepository extends JpaRepository<Member, Long> {
	Optional<Member> findMemberById(Long id);
}
