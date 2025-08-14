package com.assu.server.domain.store.service;

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
import com.assu.server.domain.store.dto.StoreResponseDTO;
import com.assu.server.domain.store.repository.StoreRepository;
import com.assu.server.domain.user.dto.StudentResponseDTO;
import com.assu.server.domain.user.entity.PartnershipUsage;
import com.assu.server.domain.user.repository.PartnershipUsageRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService{

	private final PartnershipUsageRepository partnershipUsageRepository;

	@Override
	@Transactional
	public StoreResponseDTO.todayBest getTodayBestStore() {
		List<String> bestStores = partnershipUsageRepository.findTodayPopularPartnership();

		return StoreResponseDTO.todayBest.builder()
			.bestStores(bestStores)
			.build();
	}



}
