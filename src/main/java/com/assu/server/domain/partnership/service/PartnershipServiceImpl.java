package com.assu.server.domain.partnership.service;

import org.springframework.stereotype.Service;

import com.assu.server.domain.common.entity.Member;
import com.assu.server.domain.partnership.converter.PartnershipConverter;
import com.assu.server.domain.partnership.dto.PartnershipRequestDTO;
import com.assu.server.domain.user.entity.PartnershipUsage;
import com.assu.server.domain.user.repository.PartnershipUsageRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class PartnershipServiceImpl implements PartnershipService {

	private final PartnershipUsageRepository partnershipUsageRepository;

	public void recordPartnershipUsage(PartnershipRequestDTO.finalRequest dto, Member member){

		PartnershipUsage usage = PartnershipConverter.toPartnershipUsage(dto, member);
		partnershipUsageRepository.save(usage);

		
	}
}
