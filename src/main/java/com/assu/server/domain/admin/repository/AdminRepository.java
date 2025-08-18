package com.assu.server.domain.admin.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.assu.server.domain.admin.entity.Admin;
import com.assu.server.domain.user.entity.enums.Department;
import com.assu.server.domain.user.entity.enums.Major;
import com.assu.server.domain.user.entity.enums.University;

public interface AdminRepository extends JpaRepository<Admin, Long> {

	// 여기 예원이 머지하고 수정
	List<Admin> findMatchingAdmins(@Param("university") University university,
		@Param("department") Department department,
		@Param("major") Major major);

	Optional<Admin> findByName(String name);


}
