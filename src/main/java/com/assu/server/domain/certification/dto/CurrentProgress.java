package com.assu.server.domain.certification.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
public class CurrentProgress {
	private int count;


	public static class CertificationNumber{
		public CertificationNumber(int count){
			this.count= count;
		}
		int count;
	}

	public static class CompletedNotification{
		public CompletedNotification(String message){
			this.message= message;
		}
		String message;
	}

}