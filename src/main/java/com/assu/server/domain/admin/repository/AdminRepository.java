package com.assu.server.domain.admin.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.assu.server.domain.admin.entity.Admin;
import com.assu.server.domain.user.entity.enums.Major;

public interface AdminRepository extends JpaRepository<Admin, Long> {

	@Query("SELECT a FROM Admin a WHERE " +
		"a.name LIKE %:university% OR " +
		"a.name LIKE %:department% OR " +
		"a.major= :major")
	List<Admin> findMatchingAdmins(@Param("university") String university,
		@Param("department") String department,
		@Param("major") Major major);

	Optional<Admin> findByName(String name);


}
