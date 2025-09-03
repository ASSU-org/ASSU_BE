package com.assu.server.domain.map.service;

import com.assu.server.domain.map.dto.MapResponseDTO;

import java.util.List;

public interface PlaceSearchService {

    List<MapResponseDTO.PlaceSuggestionDTO> unifiedSearch(String query, Integer size);
}
