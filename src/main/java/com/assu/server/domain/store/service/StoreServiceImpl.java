package com.assu.server.domain.store.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.assu.server.domain.store.dto.StoreResponseDTO;
import com.assu.server.domain.store.repository.StoreRepository;
import com.assu.server.domain.user.repository.PartnershipUsageRepository;

import jakarta.transaction.Transactional;


@Service
@Transactional
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
