package com.assu.server.domain.store.service;

import com.assu.server.domain.store.dto.StoreResponseDTO;

public interface StoreService {
    StoreResponseDTO.WeeklyRankResponseDTO getWeeklyRank();
    StoreResponseDTO.ListWeeklyRankResponseDTO getListWeeklyRank();
}
