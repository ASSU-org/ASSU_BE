package com.assu.server.domain.certification.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

// public class CurrentProgress {
// 	private int count;
//
//
// 	@Getter
// 	public static class CertificationNumber{
// 		public CertificationNumber(int count){
// 			this.count= count;
// 		}
//
// 		int count;
// 	}
//
// 	@Getter
// 	public static class CompletedNotification{
// 		public CompletedNotification(String message, List<Long> userIds){
//
// 			this.message= message;
// 			this.userIds= userIds;
// 		}
// 		String message;
// 		List<Long> userIds;
// 	}

// }
@Getter
@AllArgsConstructor
public class CertificationProgressResponseDTO {
	private String type;
	private Integer count;
	private String message;
	private List<Long> userIds;

	// 생성자들
	public static CertificationProgressResponseDTO progress(int count) {
		return new CertificationProgressResponseDTO("progress", count, null, null);
	}

	public static CertificationProgressResponseDTO completed(String message, List<Long> userIds) {
		return new CertificationProgressResponseDTO("completed", userIds.size(), message, userIds);
	}
}