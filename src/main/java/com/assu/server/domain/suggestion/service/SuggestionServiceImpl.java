package com.assu.server.domain.suggestion.service;

import com.assu.server.domain.admin.entity.Admin;
import com.assu.server.domain.admin.repository.AdminRepository;
import com.assu.server.domain.store.entity.Store;
import com.assu.server.domain.suggestion.converter.SuggestionConverter;
import com.assu.server.domain.suggestion.dto.SuggestionRequestDTO;
import com.assu.server.domain.suggestion.dto.SuggestionResponseDTO;
import com.assu.server.domain.suggestion.entity.Suggestion;
import com.assu.server.domain.suggestion.repository.SuggestionRepository;
import com.assu.server.domain.user.entity.Student;
import com.assu.server.domain.user.repository.StudentRepository;
import com.assu.server.global.apiPayload.code.status.ErrorStatus;
import com.assu.server.global.exception.exception.DatabaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SuggestionServiceImpl implements SuggestionService {

    private final SuggestionRepository suggestionRepository;
    private final AdminRepository adminRepository;
    private final StudentRepository studentRepository;

    @Override
    public SuggestionResponseDTO.WriteSuggestionResponseDTO writeSuggestion(SuggestionRequestDTO.WriteSuggestionRequestDTO request) {
//        Long memberId = SecurityUtil.getCurrentUserId;
        Long memberId = 1L;
        Admin admin = adminRepository.findById(request.getAdminId())
                .orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_ADMIN));

        Student student = studentRepository.findById(memberId)
                .orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_MEMBER));

        Suggestion suggestion = SuggestionConverter.toSuggestionEntity(request, admin, student);
        suggestionRepository.save(suggestion);

        return SuggestionConverter.writeSuggestionResultDTO(suggestion);
    }
}
