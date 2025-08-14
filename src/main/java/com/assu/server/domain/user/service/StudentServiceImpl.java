package com.assu.server.domain.user.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.assu.server.domain.partnership.entity.PaperContent;
import com.assu.server.domain.partnership.entity.enums.OptionType;
import com.assu.server.domain.partnership.repository.PaperContentRepository;
import com.assu.server.domain.partnership.service.PaperQueryService;
import com.assu.server.domain.user.dto.StudentResponseDTO;
import com.assu.server.domain.user.entity.PartnershipUsage;
import com.assu.server.domain.user.repository.PartnershipUsageRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

	private final PaperContentRepository paperContentRepository;
	private final PartnershipUsageRepository partnershipUsageRepository;

	@Override
	@Transactional
	public StudentResponseDTO.myPartnership getMyPartnership(Long studentId, int year, int month) {
		List<PartnershipUsage> usages = partnershipUsageRepository.findByYearAndMonth(studentId, year, month);

		return StudentResponseDTO.myPartnership.builder()
			.serviceCount(usages.size())
			.details(usages.stream()
				.map(u -> StudentResponseDTO.UsageDetailDTO.builder()
					.storeName(u.getPlace())
					.usedAt(u.getDate())
					.benefitDescription(u.getPartnershipContent())
					.isReviewed(u.getIsReviewed())
					.build()
				).toList()
			)
			.build();
	}
}
