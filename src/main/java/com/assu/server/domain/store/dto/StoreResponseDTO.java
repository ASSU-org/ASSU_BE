package com.assu.server.domain.store.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class StoreResponseDTO {

	@AllArgsConstructor
	@RequiredArgsConstructor
	@Builder
	@Getter
	public static class todayBest{
		List<String> bestStores;
	}
}
