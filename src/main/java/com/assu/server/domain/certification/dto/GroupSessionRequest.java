package com.assu.server.domain.certification.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
public class GroupSessionRequest {
	Long adminId;
	Long sessionId;

	@Override
	public String toString() {
		return "GroupSessionRequest{" +
			"adminId=" + adminId +
			", sessionId=" + sessionId +
			'}';
	}
}