package com.assu.server.domain.suggestion.service;

import com.assu.server.domain.admin.entity.Admin;
import com.assu.server.domain.admin.repository.AdminRepository;
import com.assu.server.domain.partnership.converter.PartnershipConverter;
import com.assu.server.domain.partnership.dto.PartnershipResponseDTO;
import com.assu.server.domain.partnership.entity.Goods;
import com.assu.server.domain.partnership.entity.Paper;
import com.assu.server.domain.partnership.entity.PaperContent;
import com.assu.server.domain.partnership.repository.PaperContentRepository;
import com.assu.server.domain.partnership.repository.PaperRepository;
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

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SuggestionServiceImpl implements SuggestionService {

    private final SuggestionRepository suggestionRepository;
    private final AdminRepository adminRepository;
    private final StudentRepository studentRepository;

    @Override
    public SuggestionResponseDTO.WriteSuggestionResponseDTO writeSuggestion(SuggestionRequestDTO.WriteSuggestionRequestDTO request) {
//        Long memberId = SecurityUtil.getCurrentUserId;
        Long memberId = 9L;
        Admin admin = adminRepository.findById(request.getAdminId())
                .orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_ADMIN));

        Student student = studentRepository.findById(memberId)
                .orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_STUDENT));

        Suggestion suggestion = SuggestionConverter.toSuggestionEntity(request, admin, student);
        suggestionRepository.save(suggestion);

        return SuggestionConverter.writeSuggestionResultDTO(suggestion);
    }

    @Override
    public List<SuggestionResponseDTO.GetSuggestionResponseDTO> getSuggestions() {
        // Long adminId = SecurityUtil.getCurrentUserId();
        Long adminId = 1L;

        List<Suggestion> list = suggestionRepository
                .findAllSuggestions(adminId);

        return SuggestionConverter.toGetSuggestionDTOList(list);
    }
}
