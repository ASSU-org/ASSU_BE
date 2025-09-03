package com.assu.server.domain.mapping.service;

import com.assu.server.domain.mapping.dto.StudentAdminResponseDTO;

public interface StudentAdminService {
    StudentAdminResponseDTO.CountAdminAuthResponseDTO getCountAdminAuth(Long memberId);
    StudentAdminResponseDTO.NewCountAdminResponseDTO getNewStudentCountAdmin(Long memberId);
    StudentAdminResponseDTO.CountUsagePersonResponseDTO getCountUsagePerson(Long memberId);
    StudentAdminResponseDTO.CountUsageResponseDTO getCountUsage(Long memberId);
    StudentAdminResponseDTO.CountUsageListResponseDTO getCountUsageList(Long memberId);
}
