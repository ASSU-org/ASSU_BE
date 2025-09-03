package com.assu.server.domain.certification.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
public class CurrentProgress {
	private int count;


	@Getter
	public static class CertificationNumber{
		public CertificationNumber(int count){

			this.count= count;
		}
		int count;
	}

	@Getter
	public static class CompletedNotification{
		public CompletedNotification(String message, List<Long> userIds){

			this.message= message;
			this.userIds= userIds;
		}
		String message;
		List<Long> userIds;
	}

}