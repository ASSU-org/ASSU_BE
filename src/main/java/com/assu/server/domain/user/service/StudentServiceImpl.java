package com.assu.server.domain.user.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.assu.server.domain.partnership.entity.PaperContent;
import com.assu.server.domain.partnership.repository.PaperContentRepository;
import com.assu.server.domain.store.entity.Store;
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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
				.map(u -> {
					// 1. partnershipUsage의 paperContentId로 paperContent를 조회합니다.
					// findById는 Optional을 반환하므로, orElse(null)로 처리합니다.
					PaperContent paperContent = paperContentRepository.findById(u.getContentId())
						.orElse(null);

					// 2. PaperContent에서 storeId를 가져옵니다.
					Store store = (paperContent != null) ? paperContent.getPaper().getStore() : null;
					LocalDateTime ld= u.getCreatedAt();
					String formatDate =ld.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

					return StudentResponseDTO.UsageDetailDTO.builder()
						.partnershipUsageId(u.getId())
						.adminName(u.getAdminName())
						.storeName(u.getPlace())
						.usedAt(formatDate)
						.benefitDescription(u.getPartnershipContent())
						.isReviewed(u.getIsReviewed())
						.storeId(store.getId()) // 3. storeId를 DTO에 매핑합니다.
						.partnerId(store.getPartner().getId())
						.build();
				}).toList()
			)
			.build();
	}


	@Override
	@Transactional
	public Page<StudentResponseDTO.UsageDetailDTO> getUnreviewedUsage(Long memberId, Pageable pageable) {
		// 프론트에서 1-based 페이지를 보낸 경우 0-based 로 보정
		pageable = PageRequest.of(
			Math.max(pageable.getPageNumber() - 1, 0),
			pageable.getPageSize(),
			pageable.getSort()
		);

		Page<PartnershipUsage> contentList =
			partnershipUsageRepository.findByUnreviewedUsage(memberId, pageable);

		return contentList.map(u -> {
			// 1. partnershipUsage의 paperContentId 로 paperContent 조회
			PaperContent paperContent = paperContentRepository.findById(u.getContentId())
				.orElse(null);

			// 2. store 추출
			Store store = (paperContent != null) ? paperContent.getPaper().getStore() : null;

			// 3. 날짜 포맷팅
			LocalDateTime ld = u.getCreatedAt();
			String formatDate = ld.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

			return StudentResponseDTO.UsageDetailDTO.builder()
				.partnershipUsageId(u.getId())
				.adminName(u.getAdminName())
				.storeName(u.getPlace())
				.usedAt(formatDate)
				.benefitDescription(u.getPartnershipContent())
				.isReviewed(u.getIsReviewed())
				.storeId((store != null) ? store.getId() : null) // store null 체크
				.partnerId((store != null && store.getPartner() != null) ? store.getPartner().getId() : null)
				.build();
		});
	}

}
