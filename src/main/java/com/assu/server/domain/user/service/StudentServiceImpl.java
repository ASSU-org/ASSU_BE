package com.assu.server.domain.user.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.assu.server.domain.admin.entity.Admin;
import com.assu.server.domain.admin.repository.AdminRepository;
import com.assu.server.domain.common.enums.ActivationStatus;
import com.assu.server.domain.partner.entity.Partner;
import com.assu.server.domain.partnership.entity.Goods;
import com.assu.server.domain.partnership.entity.Paper;
import com.assu.server.domain.partnership.entity.PaperContent;
import com.assu.server.domain.partnership.entity.enums.OptionType;
import com.assu.server.domain.partnership.repository.GoodsRepository;
import com.assu.server.domain.partnership.repository.PaperContentRepository;
import com.assu.server.domain.partnership.repository.PaperRepository;
import com.assu.server.domain.store.entity.Store;
import com.assu.server.domain.user.converter.StudentConverter;
import com.assu.server.domain.user.dto.StudentResponseDTO;
import com.assu.server.domain.user.entity.PartnershipUsage;
import com.assu.server.domain.user.entity.Student;
import com.assu.server.domain.user.entity.UserPaper;
import com.assu.server.domain.user.repository.PartnershipUsageRepository;
import com.assu.server.domain.user.repository.StudentRepository;
import com.assu.server.domain.user.repository.UserPaperRepository;
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
	private final UserPaperRepository userPaperRepository;
	private final PaperContentRepository paperContentRepository;
	private final PartnershipUsageRepository partnershipUsageRepository;
	private final GoodsRepository goodsRepository;
	private final AdminRepository adminRepository;
	private final PaperRepository paperRepository;

    @Override
    @Transactional
    public StudentResponseDTO.CheckStampResponseDTO getStamp(Long memberId) {
        Student student = studentRepository.findById(memberId)
                .orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_STUDENT));

        return StudentConverter.checkStampResponseDTO(student, "스탬프 조회 성공");
    }

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

	@Override
	public List<StudentResponseDTO.UsablePartnershipDTO> getUsablePartnership(Long memberId, Boolean all) {
		LocalDate today = LocalDate.now();

		List<UserPaper> userPapers = userPaperRepository.findActivePartnershipsByStudentId(memberId, today);

		List<StudentResponseDTO.UsablePartnershipDTO> result = userPapers.stream().map(up -> {
			Paper paper = up.getPaper();
			PaperContent content = up.getPaperContent();
			Store store = paper.getStore();

			String adminName = (paper.getAdmin() != null) ? paper.getAdmin().getName() : null;
			String partnerName = (store != null) ? store.getName() : null;

			// 카테고리 결정 로직 그대로
			String finalCategory = null;
			if (content != null) {
				if (content.getCategory() != null) {
					finalCategory = content.getCategory();
				} else if (content.getOptionType() == OptionType.SERVICE) {
					List<Goods> goods = goodsRepository.findByContentId(content.getId());
					if (!goods.isEmpty()) {
						finalCategory = goods.get(0).getBelonging();
					}
				}
			}

			return StudentResponseDTO.UsablePartnershipDTO.builder()
					.partnershipId(paper.getId())
					.adminName(adminName)
					.partnerName(partnerName)
					.criterionType(content != null ? content.getCriterionType() : null)
					.optionType(content != null ? content.getOptionType() : null)
					.people(content != null ? content.getPeople() : null)
					.cost(content != null ? content.getCost() : null)
					.category(finalCategory)
					.discountRate(content != null ? content.getDiscount() : null)
					.build();
		}).toList();

		return Boolean.FALSE.equals(all) ? result.stream().limit(2).toList() : result;
	}

	@Transactional
	public void syncUserPapersForStudent(Long studentId) {
		Student student = studentRepository.findById(studentId)
				.orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_STUDENT));

		// 1. 학생 기준으로 admin 찾기
		List<Admin> admins = adminRepository.findMatchingAdmins(
				student.getUniversity(),
				student.getDepartment(),
				student.getMajor()
		);

		if (admins.isEmpty()) {
			return;
		}

		List<Long> adminIds = admins.stream().map(Admin::getId).toList();
		LocalDate today = LocalDate.now();

		// 2. admin들이 만든 오늘 유효한 paper 조회
		List<Paper> papers = paperRepository.findActivePapersByAdminIds(
				adminIds,
				today,
				ActivationStatus.ACTIVE
		);

		// 3. user_paper에 없으면 넣기
		for (Paper paper : papers) {
			boolean exists = userPaperRepository.existsByStudentIdAndPaperId(studentId, paper.getId());
			if (exists) continue;

			PaperContent latestContent = paperContentRepository
					.findTopByPaperIdOrderByIdDesc(paper.getId())
					.orElse(null);

			UserPaper up = UserPaper.builder()
					.paper(paper)
					.paperContent(latestContent)
					.student(student)
					.build();

			userPaperRepository.save(up);
		}
	}

	/**
	 * 전체 학생에 대해 일괄로 user_paper 채워 넣는 메서드
	 * (스케줄러에서 이거만 호출하면 됨)
	 */
	@Transactional
	@Override
	public void syncUserPapersForAllStudents() {
		List<Student> students = studentRepository.findAll();
		for (Student s : students) {
			syncUserPapersForStudent(s.getId());
		}
	}
}

