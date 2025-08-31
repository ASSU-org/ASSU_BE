package com.assu.server.domain.mapping.service;

import com.assu.server.domain.admin.entity.Admin;
import com.assu.server.domain.admin.repository.AdminRepository;
import com.assu.server.domain.mapping.converter.StudentAdminConverter;
import com.assu.server.domain.mapping.dto.StudentAdminResponseDTO;
import com.assu.server.domain.mapping.repository.StudentAdminRepository;
import com.assu.server.domain.partnership.entity.Paper;
import com.assu.server.domain.partnership.repository.PartnershipRepository;
import com.assu.server.domain.user.service.StudentService;
import com.assu.server.global.apiPayload.code.status.ErrorStatus;
import com.assu.server.global.exception.DatabaseException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class StudentAdminServiceImpl implements StudentAdminService {
    private final StudentAdminRepository studentAdminRepository;
    private final AdminRepository adminRepository;
    private final PartnershipRepository partnershipRepository;

    @Override
    @Transactional
    public StudentAdminResponseDTO.CountAdminAuthResponseDTO getCountAdminAuth(Long memberId) {

        Long total = studentAdminRepository.countAllByAdminId(memberId);
        Admin admin = adminRepository.findById(memberId)
                .orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_ADMIN));
        String adminName = admin.getName();

        return StudentAdminConverter.countAdminAuthDTO(memberId, total, adminName);
    }
    @Override
    @Transactional
    public StudentAdminResponseDTO.NewCountAdminResponseDTO getNewStudentCountAdmin(Long memberId) {

        Long total = studentAdminRepository.countThisMonthByAdminId(memberId);
        Admin admin = adminRepository.findById(memberId)
                .orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_ADMIN));
        String adminName = admin.getName();
        return StudentAdminConverter.newCountAdminResponseDTO(memberId, total, adminName);
    }

    @Override
    @Transactional
    public StudentAdminResponseDTO.CountUsagePersonResponseDTO getCountUsagePerson(Long memberId) {

        Long total = studentAdminRepository.countTodayUsersByAdmin(memberId);
        Admin admin = adminRepository.findById(memberId)
                .orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_ADMIN));
        String adminName =admin.getName();
        return StudentAdminConverter.countUsagePersonDTO(memberId, total, adminName);
    }

    @Override
    @Transactional
    public StudentAdminResponseDTO.CountUsageResponseDTO getCountUsage(Long memberId) {
        Admin admin = adminRepository.findById(memberId)
                .orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_ADMIN));
        String adminName =admin.getName();
        List<StudentAdminRepository.StoreUsage> storeUsages = studentAdminRepository.findUsageByStore(memberId);
        var top = storeUsages.get(0);
        Paper paper = partnershipRepository.findFirstByAdmin_IdAndStore_IdOrderByIdAsc(memberId, top.getStoreId())
                .orElseThrow(() -> new DatabaseException(ErrorStatus.NO_PAPER_FOR_STORE));
        Long total = top.getUsageCount();

        return StudentAdminConverter.countUsageResponseDTO(admin, paper, total);
    }

    @Override
    @Transactional
    public StudentAdminResponseDTO.CountUsageListResponseDTO getCountUsageList(Long memberId) {

        Admin admin = adminRepository.findById(memberId)
                .orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_ADMIN));
        List<StudentAdminRepository.StoreUsage> storeUsages = studentAdminRepository.findUsageByStore(memberId);
        var items = storeUsages.stream().map(row -> {
            Paper paper = partnershipRepository.findFirstByAdmin_IdAndStore_IdOrderByIdAsc(memberId, row.getStoreId())
                    .orElseThrow(() -> new DatabaseException(ErrorStatus.NO_PAPER_FOR_STORE));
            return StudentAdminConverter.countUsageResponseDTO(admin, paper, row.getUsageCount());
        }).toList();
        return StudentAdminConverter.countUsageListResponseDTO(items);
    }

}
