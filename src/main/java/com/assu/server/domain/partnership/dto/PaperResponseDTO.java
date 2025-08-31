package com.assu.server.domain.partnership.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class PaperResponseDTO {
	@Builder
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class partnershipContent{
		String storeName;
		Long storeId;
		List<PaperContentResponseDTO.storePaperContentResponse> contents;
	}
}
