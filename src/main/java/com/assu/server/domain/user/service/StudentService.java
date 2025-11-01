package com.assu.server.domain.user.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.assu.server.domain.user.dto.StudentResponseDTO;

import java.util.List;

public interface StudentService {
	StudentResponseDTO.myPartnership getMyPartnership(Long studentId, int year, int month);
    StudentResponseDTO.CheckStampResponseDTO getStamp(Long memberId);//조회
	Page<StudentResponseDTO.UsageDetailDTO> getUnreviewedUsage(Long memberId, Pageable pageable);
	List<StudentResponseDTO.UsablePartnershipDTO> getUsablePartnership(Long memberId, Boolean all);
	void syncUserPapersForAllStudents();
}
