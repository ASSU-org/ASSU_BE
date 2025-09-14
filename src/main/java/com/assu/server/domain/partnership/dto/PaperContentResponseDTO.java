package com.assu.server.domain.partnership.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class PaperContentResponseDTO {
	@Builder
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class storePaperContentResponse{
		Long adminId;
		String adminName;
		String paperContent;
		Long contentId;
		List<String> goods;
		Integer people;
		Long cost;
	}
}
