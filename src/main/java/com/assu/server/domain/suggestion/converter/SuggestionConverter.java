package com.assu.server.domain.suggestion.converter;

import com.assu.server.domain.admin.entity.Admin;
import com.assu.server.domain.auth.entity.SSUAuth;
import com.assu.server.domain.auth.exception.CustomAuthException;
import com.assu.server.domain.store.entity.Store;
import com.assu.server.domain.suggestion.dto.SuggestionRequestDTO;
import com.assu.server.domain.suggestion.dto.SuggestionResponseDTO;
import com.assu.server.domain.suggestion.entity.Suggestion;
import com.assu.server.domain.user.entity.Student;
import com.assu.server.global.apiPayload.code.status.ErrorStatus;

import java.util.List;
import java.util.stream.Collectors;

public class SuggestionConverter {

    public static SuggestionResponseDTO.WriteSuggestionResponseDTO writeSuggestionResultDTO(Suggestion suggestion){
        return SuggestionResponseDTO.WriteSuggestionResponseDTO.builder()
                .suggestionId(suggestion.getId())
                .userId(suggestion.getStudent().getId())
                .adminId(suggestion.getAdmin().getId())
                .storeName(suggestion.getStoreName())
                .suggestionBenefit(suggestion.getContent())
                .build();
    }

    public static Suggestion toSuggestionEntity(SuggestionRequestDTO.WriteSuggestionRequestDTO suggestionRequestDTO, Admin admin, Student student){
        return Suggestion.builder()
                .admin(admin)
                .student(student)
                .storeName(suggestionRequestDTO.getStoreName())
                .content(suggestionRequestDTO.getBenefit())
                .build();
    }

    public static SuggestionResponseDTO.GetSuggestionResponseDTO GetSuggestionResultDTO(Suggestion s){

        Student student = s.getStudent();
        return SuggestionResponseDTO.GetSuggestionResponseDTO.builder()
                .suggestionId(s.getId())
                .createdAt(s.getCreatedAt())
                .storeName(s.getStoreName())
                .content(s.getContent())
                .enrollmentStatus(student.getEnrollmentStatus())
                .studentMajor(student.getMajor())
                .build();
    }

    public static List<SuggestionResponseDTO.GetSuggestionResponseDTO> toGetSuggestionDTOList(List<Suggestion> list) {
        return list.stream()
                .map(SuggestionConverter::GetSuggestionResultDTO)
                .collect(Collectors.toList());
    }

    public static SuggestionResponseDTO.GetSuggestionAdminsDTO toGetSuggestionAdmins(Admin universityAdmin, Admin departmentAdmin, Admin majorAdmin) {
        return SuggestionResponseDTO.GetSuggestionAdminsDTO.builder()
                .adminId(universityAdmin != null ? universityAdmin.getId() : null)
                .adminName(universityAdmin != null ? universityAdmin.getName() : null)
                .departId(departmentAdmin != null ? departmentAdmin.getId() : null)
                .departName(departmentAdmin != null ? departmentAdmin.getName() : null)
                .majorId(majorAdmin != null ? majorAdmin.getId() : null)
                .majorName(majorAdmin != null ? majorAdmin.getName() : null)
                .build();
    }
}
