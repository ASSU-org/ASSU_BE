package com.assu.server.domain.map.converter;

import com.assu.server.domain.map.dto.MapResponseDTO;
import com.assu.server.domain.map.entity.Location;

public class MapConverter {

    public static MapResponseDTO.SavePinResponseDTO toSavePinResponseDTO(Location location) {
        return MapResponseDTO.SavePinResponseDTO.builder()
                .pinId(location.getId())
                .ownerType(location.getOwnerType().name())
                .ownerId(location.getOwnerId())
                .name(location.getName())
                .address(location.getRoadAddress() != null ? location.getRoadAddress() : location.getAddress())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .build();
    }
}
