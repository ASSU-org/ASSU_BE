package com.assu.server.domain.mapping.service;

import com.assu.server.domain.mapping.dto.StudentAdminResponseDTO;

public interface StudentAdminService {
    StudentAdminResponseDTO.CountAdminAuthResponseDTO getCountAdminAuth();
    StudentAdminResponseDTO.NewCountAdminResponseDTO getNewStudentCountAdmin();
    StudentAdminResponseDTO.CountUsagePersonResponseDTO getCountUsagePerson();
}
