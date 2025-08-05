package com.assu.server.domain.suggestion.converter;

import com.assu.server.domain.admin.entity.Admin;
import com.assu.server.domain.store.entity.Store;
import com.assu.server.domain.suggestion.dto.SuggestionRequestDTO;
import com.assu.server.domain.suggestion.dto.SuggestionResponseDTO;
import com.assu.server.domain.suggestion.entity.Suggestion;
import com.assu.server.domain.user.entity.Student;

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
//        여기서 뭘 할거냐면 사용자에게서 데이터를 받았으면 걔를 return 하면서 entity로
        return Suggestion.builder()
                .admin(admin)
                .student(student)
                .storeName(suggestionRequestDTO.getStoreName())
                .content(suggestionRequestDTO.getBenefit())
                .build();
    }
}
