package com.assu.server.domain.store.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.assu.server.domain.partnership.entity.PaperContent;
import com.assu.server.domain.partnership.entity.enums.OptionType;
import com.assu.server.domain.partnership.repository.PaperContentRepository;
import com.assu.server.domain.store.dto.StoreResponseDTO;
import com.assu.server.domain.store.repository.StoreRepository;
import com.assu.server.domain.user.dto.StudentResponseDTO;
import com.assu.server.domain.user.entity.PartnershipUsage;
import com.assu.server.domain.user.repository.PartnershipUsageRepository;

import jakarta.transaction.Transactional;


@Service
@Transactional
public class StoreServiceImpl implements StoreService{

	private PartnershipUsageRepository partnershipUsageRepository;
	private PaperContentRepository paperContentRepository;

	@Override
	@Transactional
	public StoreResponseDTO.todayBest getTodayBestStore() {
		List<String> bestStores = partnershipUsageRepository.findTodayPopularPartnership();

		return StoreResponseDTO.todayBest.builder()
			.bestStores(bestStores)
			.build();
	}

	@Override
	@Transactional
	public StudentResponseDTO.myPartnership getMyPartnership(Long studentId, int year, int month) {
		// List<PartnershipUsage> usages = partnershipUsageRepository.findByStudentAndCreatedAtMonth(studentId, year, month);
		//
		// Set<Long> contentIds = usages.stream()
		// 	.map(PartnershipUsage::getContentId)
		// 	.filter(Objects::nonNull)
		// 	.collect(Collectors.toSet());
		//
		// Map<Long, PaperContent> contentMap = paperContentRepository.findAllById(contentIds)
		// 	.stream()
		// 	.collect(Collectors.toMap(PaperContent::getId, Function.identity()));
		//
		// long serviceCount = 0;
		// int totalDiscount = 0;
		// List<StudentResponseDTO.UsageDetailDTO> usageDetails = new ArrayList<>();
		//
		// for (PartnershipUsage usage : usages) {
		// 	PaperContent content = contentMap.get(usage.getContentId());
		// 	if (content == null) continue;
		//
		// 	String desc;
		// 	if (content.getOptionType() == OptionType.SERVICE) {
		// 		serviceCount++;
		// 		desc = String.format("%s에서 %d명 서비스 제공받았어요!", content.getCategory(), content.getPeople());
		// 	} else {
		// 		int discount = usage.getDiscount() != null ? usage.getDiscount() : 0;
		// 		totalDiscount += discount;
		// 		desc = String.format("%,d원 할인 혜택을 받았어요!", discount);
		// 	}
		//
		// 	usageDetails.add(new StudentResponseDTO.UsageDetailDTO(
		// 		usage.getPlace(),
		// 		usage.getDate(),
		// 		desc
		// 	));}
	return null;
	}

}
