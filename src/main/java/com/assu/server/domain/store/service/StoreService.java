package com.assu.server.domain.store.service;

import com.assu.server.domain.partnership.dto.PartnershipResponseDTO;
import com.assu.server.domain.store.dto.StoreResponseDTO;
import com.assu.server.domain.user.dto.StudentResponseDTO;

public interface StoreService {
	StoreResponseDTO.todayBest getTodayBestStore();
}
