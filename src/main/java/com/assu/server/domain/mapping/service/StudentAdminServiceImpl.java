package com.assu.server.domain.mapping.service;

import com.assu.server.domain.admin.entity.Admin;
import com.assu.server.domain.admin.repository.AdminRepository;
import com.assu.server.domain.mapping.converter.StudentAdminConverter;
import com.assu.server.domain.mapping.dto.StudentAdminResponseDTO;
import com.assu.server.domain.mapping.repository.StudentAdminRepository;
import com.assu.server.domain.partnership.entity.Paper;
import com.assu.server.domain.partnership.repository.PaperRepository;
import com.assu.server.domain.partnership.repository.PartnershipRepository;
import com.assu.server.domain.user.service.StudentService;
import com.assu.server.global.apiPayload.code.status.ErrorStatus;
import com.assu.server.global.exception.DatabaseException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class StudentAdminServiceImpl implements StudentAdminService {
    private final StudentAdminRepository studentAdminRepository;
    private final AdminRepository adminRepository;
    private final PaperRepository paperRepository;  // 🔧 수정: PaperRepository 사용

    @Override
    @Transactional
    public StudentAdminResponseDTO.CountAdminAuthResponseDTO getCountAdminAuth(Long memberId) {
        Admin admin = getAdminOrThrow(memberId);
        Long total = studentAdminRepository.countAllByAdminId(memberId);

        return StudentAdminConverter.countAdminAuthDTO(memberId, total, admin.getName());
    }

    @Override
    @Transactional
    public StudentAdminResponseDTO.NewCountAdminResponseDTO getNewStudentCountAdmin(Long memberId) {
        Admin admin = getAdminOrThrow(memberId);
        Long total = studentAdminRepository.countThisMonthByAdminId(memberId);

        return StudentAdminConverter.newCountAdminResponseDTO(memberId, total, admin.getName());
    }

    @Override
    @Transactional
    public StudentAdminResponseDTO.CountUsagePersonResponseDTO getCountUsagePerson(Long memberId) {
        Admin admin = getAdminOrThrow(memberId);
        Long total = studentAdminRepository.countTodayUsersByAdmin(memberId);

        return StudentAdminConverter.countUsagePersonDTO(memberId, total, admin.getName());
    }

    @Override
    @Transactional
    public StudentAdminResponseDTO.CountUsageResponseDTO getCountUsage(Long memberId) {
        Admin admin = getAdminOrThrow(memberId);

        // 🔧 수정: Paper 정보를 포함한 조회 (N+1 해결)
        List<StudentAdminRepository.StoreUsageWithPaper> storeUsages =
                studentAdminRepository.findUsageByStoreWithPaper(memberId);

        // 데이터가 없으면 예외 처리
        if (storeUsages.isEmpty()) {
            throw new DatabaseException(ErrorStatus.NO_USAGE_DATA);
        }

        // 첫 번째가 가장 사용량이 많은 업체 (ORDER BY usageCount DESC)
        var top = storeUsages.get(0);

        // 🔧 수정: Paper ID로 직접 조회 (별도 쿼리 불필요)
        Paper paper = paperRepository.findById(top.getPaperId())
                .orElseThrow(() -> new DatabaseException(ErrorStatus.NO_PAPER_FOR_STORE));

        return StudentAdminConverter.countUsageResponseDTO(admin, paper, top.getUsageCount());
    }

    @Override
    @Transactional
    public StudentAdminResponseDTO.CountUsageListResponseDTO getCountUsageList(Long memberId) {
        Admin admin = getAdminOrThrow(memberId);

        // 🔧 핵심 수정: Paper 정보를 포함한 조회 (N+1 해결)
        List<StudentAdminRepository.StoreUsageWithPaper> storeUsages =
                studentAdminRepository.findUsageByStoreWithPaper(memberId);

        if (storeUsages.isEmpty()) {
            // 빈 리스트 반환 (선택: 예외 처리도 가능)
            return StudentAdminConverter.countUsageListResponseDTO(List.of());
        }

        // 🔧 핵심 개선: Paper ID 목록을 한 번에 조회 (Batch Query)
        List<Long> paperIds = storeUsages.stream()
                .map(StudentAdminRepository.StoreUsageWithPaper::getPaperId)
                .toList();

        // 🔧 한 번의 IN 쿼리로 모든 Paper 조회
        Map<Long, Paper> paperMap = paperRepository.findAllById(paperIds).stream()
                .collect(Collectors.toMap(Paper::getId, paper -> paper));

        // 🔧 Paper 조회 없이 매핑만 수행 (N+1 완전 해결)
        var items = storeUsages.stream().map(row -> {
            Paper paper = paperMap.get(row.getPaperId());
            if (paper == null) {
                throw new DatabaseException(ErrorStatus.NO_PAPER_FOR_STORE);
            }
            return StudentAdminConverter.countUsageResponseDTO(admin, paper, row.getUsageCount());
        }).toList();

        return StudentAdminConverter.countUsageListResponseDTO(items);
    }

    //  Admin 조회 중복 제거
    private Admin getAdminOrThrow(Long adminId) {
        return adminRepository.findById(adminId)
                .orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_ADMIN));
    }
}