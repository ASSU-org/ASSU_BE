package com.assu.server.domain.mapping.converter;

import com.assu.server.domain.mapping.dto.StudentAdminResponseDTO;

public class StudentAdminConverter {

    public static StudentAdminResponseDTO.CountAdminAuthResponseDTO countAdminAuthDTO(Long adminId, Long total, String adminName) {
        return StudentAdminResponseDTO.CountAdminAuthResponseDTO.builder()
            .adminId(adminId)
            .studentCount(total)
            .adminName(adminName)
            .build();
    }

    public static StudentAdminResponseDTO.NewCountAdminResponseDTO newCountAdminResponseDTO(Long adminId, Long total, String adminName){
        return StudentAdminResponseDTO.NewCountAdminResponseDTO.builder()
                .adminId(adminId)
                .newStudentCount(total)
                .adminName(adminName)
                .build();
    }
}
