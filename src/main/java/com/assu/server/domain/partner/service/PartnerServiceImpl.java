package com.assu.server.domain.partner.service;

import com.assu.server.domain.admin.entity.Admin;
import com.assu.server.domain.admin.repository.AdminRepository;
import com.assu.server.domain.partner.dto.PartnerResponseDTO;
import com.assu.server.domain.partner.entity.Partner;
import com.assu.server.domain.partner.repository.PartnerRepository;
import com.assu.server.global.apiPayload.code.status.ErrorStatus;

import com.assu.server.global.exception.DatabaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PartnerServiceImpl implements PartnerService {

    private final PartnerRepository partnerRepository;
    private final AdminRepository adminRepository;

    @Override
    public PartnerResponseDTO.RandomAdminResponseDTO getRandomAdmin(Long partnerId) {
        Partner partner = partnerRepository.findById(partnerId)
                .orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_PARTNER));

        long total = adminRepository.countPartner(partnerId);
        if (total == 0) {
            throw new DatabaseException(ErrorStatus.NO_SUCH_ADMIN);
        }

        int limit = (int) Math.min(2, total);

        int offset = 0;
        if (total > 2) {
            offset = ThreadLocalRandom.current().nextInt(0, (int)(total - limit + 1));
        }

        List<Admin> picked = adminRepository.findPartnerWithOffset(partner.getId(), offset, limit);

        List<PartnerResponseDTO.AdminLiteDTO> admins = picked.stream()
                .map(a -> PartnerResponseDTO.AdminLiteDTO.builder()
                        .adminId(a.getId())
                        .adminAddress(a.getOfficeAddress())
                        .adminDetailAddress(a.getDetailAddress())
                        .adminName(a.getName())
                        .adminUrl(a.getMember().getProfileUrl())
                        .build())
                .collect(Collectors.toList());

        return PartnerResponseDTO.RandomAdminResponseDTO.builder()
                .admins(admins)
                .build();
    }

}
