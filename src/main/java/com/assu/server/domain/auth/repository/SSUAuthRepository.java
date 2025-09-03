package com.assu.server.domain.auth.repository;

import com.assu.server.domain.auth.entity.SSUAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SSUAuthRepository extends JpaRepository<SSUAuth, Long> {
    boolean existsByStudentNumber(String studentNumber);

    Optional<SSUAuth> findByStudentNumber(String studentNumber);
}
