package com.assu.server.domain.certification.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import com.assu.server.domain.certification.dto.GroupSessionRequest;
import com.assu.server.domain.certification.service.CertificationService;
import com.assu.server.domain.member.entity.Member;
import com.assu.server.global.util.PrincipalDetails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller // STOMP 메시지 처리를 위한 컨트롤러
@RequiredArgsConstructor
public class GroupCertificationController {

	private final CertificationService certificationService;

	@MessageMapping("/certify")
	public void certifyGroup(@Payload GroupSessionRequest dto, SimpMessageHeaderAccessor headerAccessor) {
		try {
			log.info("### SUCCESS ### 인증 요청 메시지 수신 - adminId: {}, sessionId: {}", dto.getAdminId(), dto.getSessionId());

			// Authentication에서 Member 정보 추출
			Authentication auth = (Authentication) headerAccessor.getUser();
			if (auth != null && auth.getPrincipal() instanceof PrincipalDetails) {
				PrincipalDetails principalDetails = (PrincipalDetails) auth.getPrincipal();
				// 실제 비즈니스 로직 호출
				certificationService.handleCertification(dto, principalDetails.getMember());
				log.info("### SUCCESS ### 그룹 인증 처리 완료");
			}
		} catch (Exception e) {
			log.error("### ERROR ### 인증 처리 실패", e);
		}
	}

	// @MessageMapping("/certify")
	// public void certifyGroup(SimpMessageHeaderAccessor headerAccessor) {
	// 	log.info("### DEBUG ### 메서드 진입!");
	// 	log.info("### DEBUG ### User: {}", headerAccessor.getUser());
	// 	log.info("### DEBUG ### SessionId: {}", headerAccessor.getSessionId());
	// }
}
