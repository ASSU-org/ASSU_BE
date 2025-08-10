package com.assu.server.domain.certification.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;


import org.springframework.stereotype.Service;

import com.assu.server.domain.admin.entity.Admin;
import com.assu.server.domain.admin.repository.AdminRepository;
import com.assu.server.domain.admin.service.AdminService;
import com.assu.server.domain.certification.SessionTimeoutManager;
import com.assu.server.domain.certification.component.CertificationSessionManager;
import com.assu.server.domain.certification.converter.CertificationConverter;
import com.assu.server.domain.certification.dto.CertificationRequestDTO;
import com.assu.server.domain.certification.dto.CertificationResponseDTO;
import com.assu.server.domain.certification.dto.CurrentProgress;
import com.assu.server.domain.certification.entity.AssociateCertification;
import com.assu.server.domain.certification.entity.QRCertification;
import com.assu.server.domain.certification.entity.enums.SessionStatus;
import com.assu.server.domain.certification.repository.AssociateCertificationRepository;
import com.assu.server.domain.certification.repository.QRCertificationRepository;
import com.assu.server.domain.common.entity.Member;
import com.assu.server.domain.store.entity.Store;
import com.assu.server.domain.store.repository.StoreRepository;
import com.assu.server.domain.user.entity.Student;
import com.assu.server.global.apiPayload.code.status.ErrorStatus;
import com.assu.server.global.exception.exception.GeneralException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;

// AdminService 참조, 순환 참조 문제 주의
@Transactional
@Service
@RequiredArgsConstructor
public class CertificationServiceImpl implements CertificationService {
	private final AdminRepository adminRepository;
	private final StoreRepository storeRepository;
	private final AssociateCertificationRepository associateCertificationRepository;

	// 세션 메니저
	private final CertificationSessionManager sessionManager;
	private final SessionTimeoutManager timeoutManager;

	// AdminService 참조
	private final AdminService adminService;
	private final SimpMessagingTemplate messagingTemplate;



	@Override
	public CertificationResponseDTO.getSessionIdResponse getSessionId(
		CertificationRequestDTO.groupRequest dto, Member member){
		Long userId = member.getId();

		// admin id 추출
		Admin admin = adminRepository.findByName(dto.getAdminName()).orElseThrow(
			() -> new GeneralException(ErrorStatus.NO_SUCH_ADMIN)
		);

		// store id 추출
		Store store = storeRepository.findByName(dto.getStoreName()).orElseThrow(
			() -> new GeneralException(ErrorStatus.NO_SUCH_STORE)
		);


		// 세션 생성 및 구독 로직
		AssociateCertification ownerCertification = associateCertificationRepository.save(
			CertificationConverter.toAssociateCertification(dto, store, member));
		Long sessionId = ownerCertification.getId();

		sessionManager.openSession(sessionId);
		// 세션 생성 직후 만료 시간을 5분으로 설정
		timeoutManager.scheduleTimeout(sessionId, Duration.ofMinutes(5));

		// 세션 여는 대표자는 제일 먼저 인증
		sessionManager.addUserToSession(sessionId, userId);

		return CertificationConverter.toSessionIdResponse(sessionId, admin.getId());

	}

	@Override
	public void handleCertification(CertificationRequestDTO.groupSessionRequest dto, Member member) {

		Long userId = member.getId();

		// 제휴 대상인지 확인하기
		Long adminId = dto.getAdminId();
		Student student = member.getStudentProfile();
		List<Admin> admins = adminService.findMatchingAdmins(student.getUniversity(), student.getDepartment(), student.getMajor());

		boolean matched = admins.stream()
			.anyMatch(admin -> admin.getId().equals(adminId));

		if (!matched) {
			throw new IllegalArgumentException("관리자 정보가 일치하지 않습니다.");
		}


		// session 존재 여부 확인
		Long sessionId = dto.getSessionId();
		AssociateCertification session = associateCertificationRepository.findById(sessionId).orElseThrow(
			() -> new GeneralException(ErrorStatus.NO_SUCH_SESSION)
		);

		// 세션 활성화 여부 확인
		if(session.getStatus() != SessionStatus.OPENED)
			throw new GeneralException(ErrorStatus.SESSION_NOT_OPENED);

		boolean isDoubledUser= sessionManager.hasUser(sessionId, userId);
		if(isDoubledUser)
			throw new GeneralException(ErrorStatus.DOUBLE_CERTIFIED_USER);

		sessionManager.addUserToSession(sessionId, userId);
		int currentCertifiedNumber = sessionManager.getCurrentUserCount(sessionId);

		messagingTemplate.convertAndSend("/certification/progress"+sessionId,
			new CurrentProgress.CertificationNumber(currentCertifiedNumber));

		if(currentCertifiedNumber >= session.getPeopleNumber()){
			session.setIsCertified(true);
			session.setStatus(SessionStatus.COMPLETED);
			associateCertificationRepository.save(session);


			messagingTemplate.convertAndSend("/certification/progress"+sessionId,
				new CurrentProgress.CompletedNotification("인증이 완료되었습니다.", sessionManager.snapshotUserIds(sessionId))
			);
		}

		// // 인증 정보를 QRCertification 에 삽입
		// QRCertification qrCertification = new QRCertification();
		// 	qrCertification.builder()
		// 	.certification(session)
		// 	.verifiedTime(LocalDateTime.now())
		// 	.isVerified(true)
		// 	.build();
		// qrRepository.save(qrCertification);

	}



}
