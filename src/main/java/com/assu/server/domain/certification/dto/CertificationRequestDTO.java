package com.assu.server.domain.certification.dto;

import lombok.Getter;


public class CertificationRequestDTO {

	@Getter
	public static class groupRequest{
		Integer people;
		Long storeId;
		Long adminId;
		Integer tableNumber;
	}

	@Getter
	public static class personalRequest{
		Long storeId;
		Long adminId;
		Integer tableNumber;
	}

	@Getter
	public static class groupSessionRequest{
		Long adminId;
		Long sessionId;
	}
}
