package com.assu.server.domain.user.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.assu.server.domain.user.dto.StudentResponseDTO;

public interface StudentService {
	StudentResponseDTO.myPartnership getMyPartnership(Long studentId, int year, int month);
    StudentResponseDTO.CheckStampResponseDTO getStamp(Long memberId);//조회

	Page<StudentResponseDTO.UsageDetailDTO> getUnreviewedUsage(Long memberId, Pageable pageable);
}
