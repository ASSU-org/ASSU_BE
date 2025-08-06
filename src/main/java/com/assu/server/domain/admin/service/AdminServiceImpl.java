package com.assu.server.domain.admin.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.assu.server.domain.admin.entity.Admin;
import com.assu.server.domain.admin.repository.AdminRepository;
import com.assu.server.domain.user.entity.enums.Major;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Transactional
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

	private final AdminRepository adminRepository;

	// 유저의 정보와 맞는 admin 을 찾기
	@Override
	public List<Admin> findMatchingAdmins(String university, String department, Major major){


		List<Admin> adminList = adminRepository.findMatchingAdmins(university, department,major);

		return adminList;
	}
}
