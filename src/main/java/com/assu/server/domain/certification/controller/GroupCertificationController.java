package com.assu.server.domain.certification.controller;

import java.security.Principal;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.assu.server.domain.certification.dto.GroupSessionRequest;
import com.assu.server.domain.certification.service.CertificationService;
import com.assu.server.domain.member.entity.Member;
import com.assu.server.global.util.PrincipalDetails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller // STOMP 메시지 처리를 위한 컨트롤러
@RequiredArgsConstructor
@Component
@RequestMapping("/app")
public class GroupCertificationController {

	private final CertificationService certificationService;

	@MessageMapping("/certify")
	public void certifyGroup(@Payload GroupSessionRequest dto,
		Principal principal) {
		if (principal instanceof UsernamePasswordAuthenticationToken) {
			UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken)principal;
			PrincipalDetails principalDetails = (PrincipalDetails)auth.getPrincipal();

			try {
				log.info("### SUCCESS ### 인증 요청 메시지 수신 - user: {}, adminId: {}, sessionId: {}",
					principalDetails.getUsername(), dto.getAdminId(), dto.getSessionId());

				// 헤더를 직접 다룰 필요 없이, 바로 principalDetails 객체를 사용
				if (principalDetails != null) {
					certificationService.handleCertification(dto, principalDetails.getMember());
					log.info("### SUCCESS ### 그룹 인증 처리 완료");
				}
			} catch (Exception e) {
				log.error("### ERROR ### 인증 처리 실패", e);
			}
		}
	}

}
