package com.assu.server.domain.suggestion.service;

import com.assu.server.domain.admin.entity.Admin;
import com.assu.server.domain.admin.repository.AdminRepository;
import com.assu.server.domain.notification.service.NotificationCommandService;
import com.assu.server.domain.suggestion.converter.SuggestionConverter;
import com.assu.server.domain.suggestion.dto.SuggestionRequestDTO;
import com.assu.server.domain.suggestion.dto.SuggestionResponseDTO;
import com.assu.server.domain.suggestion.entity.Suggestion;
import com.assu.server.domain.suggestion.repository.SuggestionRepository;
import com.assu.server.domain.user.entity.Student;
import com.assu.server.domain.user.repository.StudentRepository;
import com.assu.server.domain.common.entity.enums.ReportedStatus;
import com.assu.server.global.apiPayload.code.status.ErrorStatus;
import com.assu.server.global.exception.DatabaseException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SuggestionServiceImpl implements SuggestionService {

    private final SuggestionRepository suggestionRepository;
    private final AdminRepository adminRepository;
    private final StudentRepository studentRepository;
    private final NotificationCommandService notificationCommandService;

    @Override
    @Transactional
    public SuggestionResponseDTO.WriteSuggestionResponseDTO writeSuggestion(SuggestionRequestDTO.WriteSuggestionRequestDTO request, Long userId) {

        Admin admin = adminRepository.findById(request.getAdminId())
                .orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_ADMIN));

        Student student = studentRepository.findById(userId)
                .orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_STUDENT));

        Suggestion suggestion = SuggestionConverter.toSuggestionEntity(request, admin, student);
        suggestionRepository.save(suggestion);
        notificationCommandService.sendPartnerSuggestion(suggestion.getAdmin().getId(), suggestion.getId());

        return SuggestionConverter.writeSuggestionResultDTO(suggestion);
    }

    @Override
    public List<SuggestionResponseDTO.GetSuggestionResponseDTO> getSuggestions(Long adminId) {
        // 신고되지 않은 건의글과 신고되지 않은 학생이 작성한 건의글만 조회
        List<Suggestion> list = suggestionRepository
                .findAllSuggestionsWithStatus(adminId, ReportedStatus.NORMAL, ReportedStatus.NORMAL);

        return SuggestionConverter.toGetSuggestionDTOList(list);
    }

    @Override
    public SuggestionResponseDTO.GetSuggestionAdminsDTO getSuggestionAdmins(Long userId) {

        Student student = studentRepository.findById(userId)
                .orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_STUDENT));

        List<Admin> adminList = adminRepository.findMatchingAdmins(
                student.getUniversity(),
                student.getDepartment(),
                student.getMajor()
        );

        Admin universityAdmin = null;
        Admin departmentAdmin = null;
        Admin majorAdmin = null;

        for (Admin admin : adminList) {
            if (admin.getMajor() != null) {
                majorAdmin = admin;
            }
            else if (admin.getDepartment() != null) {
                departmentAdmin = admin;
            }
            else {
                universityAdmin = admin;
            }
        }

        return SuggestionConverter.toGetSuggestionAdmins(universityAdmin, departmentAdmin, majorAdmin);
    }
}
