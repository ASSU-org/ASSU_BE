package com.assu.server.domain.certification;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.assu.server.domain.certification.component.CertificationSessionManager;
import com.assu.server.domain.certification.entity.AssociateCertification;
import com.assu.server.domain.certification.entity.enums.SessionStatus;
import com.assu.server.domain.certification.repository.AssociateCertificationRepository;

@Component
public class SessionTimeoutManager {

	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	@Autowired
	private AssociateCertificationRepository certificationRepository;

	@Autowired
	private CertificationSessionManager sessionManager;

	public void scheduleTimeout(Long sessionId, Duration timeout) {
		scheduler.schedule(() -> {
			closeSession(sessionId);
		}, timeout.toMillis(), TimeUnit.MILLISECONDS);
	}

	private void closeSession(Long sessionId) {
		Optional<AssociateCertification> certOpt = certificationRepository.findById(sessionId);
		certOpt.ifPresent(cert -> {
			if (cert.getStatus() == SessionStatus.OPENED) {
				cert.setStatus(SessionStatus.EXPIRED);
				certificationRepository.save(cert);
			}
		});
		// 이러면 인증 전에 만료되는 것은 EXPIRED로, 시간안에 인증 된 세션은 COMPLETED로 남음

		// 메모리에서도 세션 제거
		sessionManager.removeSession(sessionId);
	}
}
