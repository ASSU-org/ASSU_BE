package com.assu.server.domain.partnership.service;

import com.assu.server.domain.partnership.dto.PartnershipRequestDTO;
import com.assu.server.domain.partnership.dto.PartnershipResponseDTO;
import com.assu.server.global.util.PrincipalDetails;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PartnershipService {

    PartnershipResponseDTO.WritePartnershipResponseDTO writePartnershipAsPartner(
            @RequestBody PartnershipRequestDTO.WritePartnershipRequestDTO request,
            Long memberId
    );

    List<PartnershipResponseDTO.WritePartnershipResponseDTO> listPartnershipsForAdmin(boolean all, Long partnerId);
    List<PartnershipResponseDTO.WritePartnershipResponseDTO> listPartnershipsForPartner(boolean all, Long adminId);


    PartnershipResponseDTO.WritePartnershipResponseDTO getPartnership(Long partnershipId);

    PartnershipResponseDTO.UpdateResponseDTO updatePartnershipStatus(Long partnershipId, PartnershipRequestDTO.UpdateRequestDTO request);

    PartnershipResponseDTO.ManualPartnershipResponseDTO createManualPartnership(
            PartnershipRequestDTO.ManualPartnershipRequestDTO request,
            Long adminId,
            MultipartFile contractImage
    );
}
