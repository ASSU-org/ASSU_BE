package com.assu.server.domain.map.service;

import com.assu.server.domain.map.dto.MapRequestDTO;
import com.assu.server.domain.map.dto.MapResponseDTO;

import java.util.List;

public interface MapService {
    List<MapResponseDTO.AdminMapResponseDTO> getAdmins(MapRequestDTO.ViewOnMapDTO viewport, Long memberId);
    List<MapResponseDTO.PartnerMapResponseDTO> getPartners(MapRequestDTO.ViewOnMapDTO viewport, Long memberId);
    List<MapResponseDTO.StoreMapResponseDTO> getStores(MapRequestDTO.ViewOnMapDTO viewport, Long memberId);

    List<MapResponseDTO.StoreMapResponseDTO>   searchStores(String keyword);
    List<MapResponseDTO.PartnerMapResponseDTO> searchPartner(String keyword, Long memberId);
    List<MapResponseDTO.AdminMapResponseDTO>   searchAdmin(String keyword, Long memberId);

    MapResponseDTO.PlaceSearchResponse search(String query, Double x, Double y, Integer radius, Integer page, Integer size, String sort);
    MapResponseDTO.ConfirmResponse confirmForAdmin(MapRequestDTO.ConfirmRequest request, Long adminId);
    MapResponseDTO.ConfirmResponse confirmForPartner(MapRequestDTO.ConfirmRequest request, Long partnerId);
}
