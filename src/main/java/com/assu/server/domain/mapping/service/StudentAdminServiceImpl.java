package com.assu.server.domain.mapping.service;

import com.assu.server.domain.admin.entity.Admin;
import com.assu.server.domain.admin.repository.AdminRepository;
import com.assu.server.domain.mapping.converter.StudentAdminConverter;
import com.assu.server.domain.mapping.dto.StudentAdminResponseDTO;
import com.assu.server.domain.mapping.repository.StudentAdminRepository;
import com.assu.server.domain.user.service.StudentService;
import com.assu.server.global.apiPayload.code.status.ErrorStatus;
import com.assu.server.global.exception.exception.DatabaseException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class StudentAdminServiceImpl implements StudentAdminService {
    private final StudentAdminRepository studentAdminRepository;
    private final AdminRepository adminRepository;

    @Override
    @Transactional
    public StudentAdminResponseDTO.CountAdminAuthResponseDTO getCountAdminAuth() {
        //Long memberId = SecurityUtil.getCurrentUserId;
        Long memberId = 6L;
        Long total = studentAdminRepository.countAllByAdminId(memberId);
        Admin admin = adminRepository.findById(memberId)
                .orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_ADMIN));
        String adminName = admin.getName();

        return StudentAdminConverter.countAdminAuthDTO(memberId, total, adminName);
    }
    @Override
    @Transactional
    public StudentAdminResponseDTO.NewCountAdminResponseDTO getNewStudentCountAdmin() {
        //Long memberId = SecurityUtil.getCurrentUserId;
        Long memberId = 5L;
        Long total = studentAdminRepository.countThisMonthByAdminId(memberId);
        Admin admin = adminRepository.findById(memberId)
                .orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_ADMIN));
        String adminName = admin.getName();
        return StudentAdminConverter.newCountAdminResponseDTO(memberId, total, adminName);
    }
}
