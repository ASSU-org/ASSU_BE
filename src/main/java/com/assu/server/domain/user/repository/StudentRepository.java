package com.assu.server.domain.user.repository;

import com.assu.server.domain.user.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Integer> {
}
