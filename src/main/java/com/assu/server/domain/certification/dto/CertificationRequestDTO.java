package com.assu.server.domain.certification.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

	// @Getter
	// @NoArgsConstructor(access = AccessLevel.PROTECTED)
	// @AllArgsConstructor
	// public static class groupSessionRequest {
	// 	private Long adminId;
	// 	private Long sessionId;
	// }
}
