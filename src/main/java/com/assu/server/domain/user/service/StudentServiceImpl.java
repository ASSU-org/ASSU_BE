package com.assu.server.domain.user.service;

import java.util.List;

import com.assu.server.domain.partnership.repository.PaperContentRepository;
import com.assu.server.domain.user.converter.StudentConverter;
import com.assu.server.domain.user.dto.StudentResponseDTO;
import com.assu.server.domain.user.entity.PartnershipUsage;
import com.assu.server.domain.user.entity.Student;
import com.assu.server.domain.user.repository.PartnershipUsageRepository;
import com.assu.server.domain.user.repository.StudentRepository;
import com.assu.server.global.apiPayload.code.status.ErrorStatus;
import com.assu.server.global.exception.DatabaseException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {
	private final StudentRepository studentRepository;
    @Override
    @Transactional
    public StudentResponseDTO.CheckStampResponseDTO getStamp(Long memberId) {
        Student student = studentRepository.findById(memberId)
                .orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_STUDENT));

        return StudentConverter.checkStampResponseDTO(student, "스탬프 조회 성공");
    }

	private final PaperContentRepository paperContentRepository;
	private final PartnershipUsageRepository partnershipUsageRepository;

	@Override
	@Transactional
	public StudentResponseDTO.myPartnership getMyPartnership(Long studentId, int year, int month) {
		List<PartnershipUsage> usages = partnershipUsageRepository.findByYearAndMonth(studentId, year, month);

		return StudentResponseDTO.myPartnership.builder()
			.serviceCount(usages.size())
			.details(usages.stream()
				.map(u -> StudentResponseDTO.UsageDetailDTO.builder()
					.storeName(u.getPlace())
					.usedAt(u.getDate())
					.benefitDescription(u.getPartnershipContent())
					.isReviewed(u.getIsReviewed())
					.build()
				).toList()
			)
			.build();
	}
}
