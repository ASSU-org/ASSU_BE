package com.assu.server.domain.certification.dto;

import lombok.Getter;


public class CertificationRequestDTO {

	@Getter
	public static class groupRequest{
		Integer people;
		String storeName;
		String adminName;
		Integer tableNumber;
	}

	@Getter
	public static class groupSessionRequest{
		Long adminId;
		Long sessionId;
	}
}
