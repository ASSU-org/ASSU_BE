package com.assu.server.domain.partnership.service;

import com.assu.server.domain.partnership.dto.PartnershipRequestDTO;
import com.assu.server.domain.partnership.dto.PartnershipResponseDTO;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

import java.util.List;

public interface PartnershipService {

    PartnershipResponseDTO.WritePartnershipResponseDTO writePartnership(
            @RequestBody PartnershipRequestDTO.WritePartnershipRequestDTO request
    );

    List<PartnershipResponseDTO.WritePartnershipResponseDTO> listPartnerships(boolean all);

    PartnershipResponseDTO.WritePartnershipResponseDTO getPartnership(Long partnershipId);

    PartnershipResponseDTO.UpdateResponseDTO updatePartnershipStatus(Long partnershipId, PartnershipRequestDTO.UpdateRequestDTO request);
}
