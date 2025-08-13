package com.assu.server.domain.store.service;

import java.util.List;

import com.assu.server.domain.store.dto.StoreResponseDTO;
import com.assu.server.domain.store.repository.StoreRepository;
import com.assu.server.domain.user.repository.PartnershipUsageRepository;

import jakarta.transaction.Transactional;

public class StoreServiceImpl implements StoreService{

	private PartnershipUsageRepository partnershipUsageRepository;

	@Override
	@Transactional
	public StoreResponseDTO.todayBest getTodayBestStore() {
		List<String> bestStores = partnershipUsageRepository.findTodayPopularPartnership();

		return StoreResponseDTO.todayBest.builder()
			.bestStores(bestStores)
			.build();
	}

}
