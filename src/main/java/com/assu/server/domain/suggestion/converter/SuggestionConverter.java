package com.assu.server.domain.suggestion.converter;

import com.assu.server.domain.admin.entity.Admin;
import com.assu.server.domain.store.entity.Store;
import com.assu.server.domain.suggestion.dto.SuggestionRequestDTO;
import com.assu.server.domain.suggestion.dto.SuggestionResponseDTO;
import com.assu.server.domain.suggestion.entity.Suggestion;
import com.assu.server.domain.user.entity.Student;

import java.util.List;
import java.util.stream.Collectors;

public class SuggestionConverter {

    public static SuggestionResponseDTO.WriteSuggestionResponseDTO writeSuggestionResultDTO(Suggestion suggestion){
        return SuggestionResponseDTO.WriteSuggestionResponseDTO.builder()
                .suggestionId(suggestion.getId())
                .memberId(suggestion.getStudent().getId())
                .studentNumber(suggestion.getStudent().getStudentNumber())
                .suggestionSubjectId(suggestion.getAdmin().getId())
                .suggestionStore(suggestion.getStoreName())
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
                .content(s.getContent())
                .studentNumber(student.getStudentNumber())
                .enrollmentStatus(student.getEnrollmentStatus())
                .studentMajor(student.getMajor())
                .build();
    }

    public static List<SuggestionResponseDTO.GetSuggestionResponseDTO> toGetSuggestionDTOList(List<Suggestion> list) {
        return list.stream()
                .map(SuggestionConverter::GetSuggestionResultDTO)
                .collect(Collectors.toList());
    }
}
