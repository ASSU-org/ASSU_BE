package com.assu.server.domain.admin.service;

import com.assu.server.domain.admin.dto.AdminResponseDTO;
import com.assu.server.domain.admin.entity.Admin;
import com.assu.server.domain.admin.repository.AdminRepository;
import com.assu.server.domain.partner.entity.Partner;
import com.assu.server.domain.partner.repository.PartnerRepository;
import com.assu.server.global.apiPayload.code.status.ErrorStatus;
import com.assu.server.global.exception.exception.DatabaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final PartnerRepository partnerRepository;

    @Override
    public AdminResponseDTO.RandomPartnerResponseDTO suggestRandomPartner() {
//        Long adminId = SecurityUtil.getCurrentId();
        Long adminId = 1L;
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_ADMIN));

        long total = partnerRepository.countUnpartneredActiveByAdmin(admin.getId());
        if (total <= 0) {
            throw new DatabaseException(ErrorStatus.NO_AVAILABLE_PARTNER);
        }

        int offset = ThreadLocalRandom.current().nextInt((int)total);

        Partner picked = partnerRepository.findUnpartneredActiveByAdminWithOffset(admin.getId(), offset);
        if(picked == null) {
            throw new DatabaseException(ErrorStatus.NO_AVAILABLE_PARTNER);
        }

        return AdminResponseDTO.RandomPartnerResponseDTO.builder()
                .partnerId(picked.getId())
                .partnerName(picked.getName())
                .partnerAddress(picked.getAddress())
                .partnerDetailAddress(picked.getDetailAddress())
                .build();

    }
}
