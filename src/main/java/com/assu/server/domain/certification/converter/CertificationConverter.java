package com.assu.server.domain.certification.converter;


import com.assu.server.domain.certification.dto.CertificationRequestDTO;
import com.assu.server.domain.certification.dto.CertificationResponseDTO;
import com.assu.server.domain.certification.entity.AssociateCertification;
import com.assu.server.domain.certification.entity.enums.SessionStatus;
import com.assu.server.domain.member.entity.Member;
import com.assu.server.domain.store.entity.Store;

public class CertificationConverter {
	public static AssociateCertification toAssociateCertification(CertificationRequestDTO.groupRequest dto, Store store, Member member) {
		return AssociateCertification.builder()
			.store(store)
			.partner(store.getPartner())
			.status(SessionStatus.OPENED)
			.isCertified(false)
			.peopleNumber(dto.getPeople())
			.tableNumber(dto.getTableNumber())
			.student(member.getStudentProfile())
			.build();
	}

	public static CertificationResponseDTO.getSessionIdResponse toSessionIdResponse(Long sessionId){
		return CertificationResponseDTO.getSessionIdResponse.builder()
			.sessionId(sessionId)
			.build();
	}

	public static AssociateCertification toPersonalCertification(CertificationRequestDTO.personalRequest dto, Store store, Member member) {
		return AssociateCertification.builder()
			.store(store)
			.partner(store.getPartner())
			.isCertified(true)
			.tableNumber(dto.getTableNumber())
			.peopleNumber(1)
			.student(member.getStudentProfile())
			.build();
	}
}
