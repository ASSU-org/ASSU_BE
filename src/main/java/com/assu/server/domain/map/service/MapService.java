package com.assu.server.domain.map.service;

import com.assu.server.domain.map.dto.MapRequestDTO;
import com.assu.server.domain.map.dto.MapResponseDTO;
import com.assu.server.domain.map.entity.Location;

import java.util.List;

public interface MapService {
    MapResponseDTO.SavePinResponseDTO saveAdminPin();
    MapResponseDTO.SavePinResponseDTO savePartnerPin();
    MapResponseDTO.SavePinResponseDTO saveStorePin(Long storeId);

    List<MapResponseDTO.AdminMapResponseDTO> getAdmins(MapRequestDTO.ViewOnMapDTO viewport);
    List<MapResponseDTO.PartnerMapResponseDTO> getPartners(MapRequestDTO.ViewOnMapDTO viewport);
    List<MapResponseDTO.StoreMapResponseDTO> getStores(MapRequestDTO.ViewOnMapDTO viewport);

    List<MapResponseDTO.StoreMapResponseDTO>   searchStores(String keyword);
    List<MapResponseDTO.PartnerMapResponseDTO> searchPartner(String keyword);
    List<MapResponseDTO.AdminMapResponseDTO>   searchAdmin(String keyword);
}
