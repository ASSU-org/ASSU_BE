package com.assu.server.domain.certification.service;

import com.assu.server.domain.certification.dto.CertificationRequestDTO;
import com.assu.server.domain.certification.dto.CertificationResponseDTO;
import com.assu.server.domain.common.entity.Member;

public interface CertificationService {

	CertificationResponseDTO.getSessionIdResponse getSessionId(CertificationRequestDTO.groupRequest dto, Member member);

	void handleCertification(CertificationRequestDTO.groupSessionRequest dto, Member member);
}
