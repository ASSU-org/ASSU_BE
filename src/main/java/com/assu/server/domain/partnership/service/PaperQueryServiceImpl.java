package com.assu.server.domain.partnership.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.assu.server.domain.admin.entity.Admin;
import com.assu.server.domain.admin.service.AdminService;
import com.assu.server.domain.common.entity.Member;
import com.assu.server.domain.common.enums.ActivationStatus;
import com.assu.server.domain.common.enums.UserRole;
import com.assu.server.domain.partnership.converter.PartnershipConverter;
import com.assu.server.domain.partnership.dto.PaperContentResponseDTO;
import com.assu.server.domain.partnership.dto.PaperResponseDTO;
import com.assu.server.domain.partnership.entity.Paper;
import com.assu.server.domain.partnership.entity.PaperContent;
import com.assu.server.domain.partnership.repository.PaperContentRepository;
import com.assu.server.domain.partnership.repository.PaperRepository;
import com.assu.server.domain.store.entity.Store;
import com.assu.server.domain.store.repository.StoreRepository;
import com.assu.server.domain.user.entity.Student;
import com.assu.server.domain.user.entity.enums.Major;
import com.assu.server.global.apiPayload.code.status.ErrorStatus;
import com.assu.server.global.exception.exception.GeneralException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PaperQueryServiceImpl implements PaperQueryService {

	private final AdminService adminService;
	private final PaperRepository paperRepository;
	private final PaperContentRepository contentRepository;
	private final StoreRepository storeRepository;

	@Override
	public PaperResponseDTO.partnershipContent getStorePaperContent(Long storeId, Member member){

		// 역할이 학생이 아닌 경우 : 이미 type별로 ui를 분기 시켜놔서 그럴일 없을 것 같긴 하지만 혹시 몰라서 처리함
		if(member.getRole() != UserRole.USER)
			throw new GeneralException(ErrorStatus.NO_STUENT_TYPE);

		Student student = member.getStudentProfile();

		// 유저의 학교, 단과대, 학부 정보를 조회하여 일치하는 admin을 찾습니다.
		List<Admin> adminList = adminService.findMatchingAdmins(
			student.getUniversity(),
			student.getDepartment(),
			student.getMajor());

		// 한번 더 거르기 위해서
		List<Admin> filteredAdmin = adminList.stream()
			.filter(admin -> {
				String name = admin.getName();
				Major major = admin.getMajor();
				return name.contains(student.getUniversity())
					|| name.contains(student.getDepartment())
					|| major.equals(student.getMajor());
			}).toList();


		// 추출한 admin, store와 일치하는 paperId 를 추출합니다.
		List<Paper> paperList = filteredAdmin.stream()
			.flatMap(admin ->
				paperRepository.findByStoreIdAndAdminIdAndStatus(storeId, admin.getId(), ActivationStatus.ACTIVE)
					.stream()).toList();

		//paperId로 paperContent 를 조회
		List<PaperContent> contentList = paperList.stream()
			.flatMap(paper->
				contentRepository.findByPaperId(paper.getId()).stream()
				).toList();



		Store store = storeRepository.findById(storeId).orElseThrow(
			() -> new GeneralException(ErrorStatus.NO_SUCH_STORE)
		);

		// dto 변환
		List<PaperContentResponseDTO.storePaperContentResponse> contents =
			PartnershipConverter.toContentResponseList(contentList);

		// partnershipContent DTO 생성
		return PaperResponseDTO.partnershipContent.builder()
			.storeName(store.getName())
			.storeId(storeId)
			.contents(contents)
			.build();


	}

}
