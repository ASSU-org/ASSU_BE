package com.assu.server.domain.partnership.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.assu.server.domain.common.entity.Member;
import com.assu.server.domain.partnership.converter.PartnershipConverter;
import com.assu.server.domain.partnership.dto.PartnershipRequestDTO;
import com.assu.server.domain.user.entity.PartnershipUsage;
import com.assu.server.domain.user.entity.Student;
import com.assu.server.domain.user.repository.PartnershipUsageRepository;
import com.assu.server.domain.user.repository.StudentRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class PartnershipServiceImpl implements PartnershipService {

	private final PartnershipUsageRepository partnershipUsageRepository;
	private final StudentRepository studentRepository;

	public void recordPartnershipUsage(PartnershipRequestDTO.finalRequest dto, Member member){


		List<PartnershipUsage> usages = new ArrayList<>();

		// 1) 요청한 member 본인
		usages.add(PartnershipConverter.toPartnershipUsage(dto, member.getStudentProfile()));

		// 2) dto의 userIds에 있는 다른 사용자들
		for (Long userId : dto.getUserIds()) {
			Student student = studentRepository.getReferenceById(userId);
			usages.add(PartnershipConverter.toPartnershipUsage(dto, student));
		}

		partnershipUsageRepository.saveAll(usages);
		
	}
}
