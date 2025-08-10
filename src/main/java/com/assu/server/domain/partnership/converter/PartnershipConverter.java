package com.assu.server.domain.partnership.converter;

import java.time.LocalDate;
import java.util.List;

import com.assu.server.domain.common.entity.Member;
import com.assu.server.domain.partnership.dto.PaperContentResponseDTO;
import com.assu.server.domain.partnership.dto.PartnershipRequestDTO;
import com.assu.server.domain.partnership.entity.Goods;
import com.assu.server.domain.partnership.entity.PaperContent;
import com.assu.server.domain.partnership.entity.enums.CriterionType;
import com.assu.server.domain.partnership.entity.enums.OptionType;
import com.assu.server.domain.user.entity.PartnershipUsage;
import com.assu.server.domain.user.entity.Student;

public class PartnershipConverter {

	public static PartnershipUsage toPartnershipUsage(PartnershipRequestDTO.finalRequest dto, Student student) {
		return PartnershipUsage.builder()
			.date(LocalDate.now())
			.place(dto.getPlaceName())
			.student(student)
			.isReviewed(false)
			.contentId(dto.getContentId())
			.partnershipContent(dto.getPartnershipContent())
			.build();
	}



	public static List<PaperContentResponseDTO.storePaperContentResponse> toContentResponseList(List<PaperContent> contents) {
		return contents.stream()
			.map(PartnershipConverter::toContentResponse)
			.toList();
	}

	public static PaperContentResponseDTO.storePaperContentResponse toContentResponse(PaperContent content) {
		List<String> goodsList = extractGoods(content);
		Integer peopleValue = extractPeople(content);
		String paperContentText = buildPaperContentText(content, goodsList, peopleValue);

		return PaperContentResponseDTO.storePaperContentResponse.builder()
			.adminName(content.getPaper().getAdmin().getName())
			.paperContent(paperContentText)
			.contentId(content.getId())
			.goods(goodsList)
			.people(peopleValue)
			.build();
	}

	private static List<String> extractGoods(PaperContent content) {
		if (content.getOptionType() == OptionType.SERVICE && content.getCategory() != null) {
			return content.getGoods().stream()
				.map(Goods::getBelonging)
				.toList();
		}
		return null;
	}

	private static Integer extractPeople(PaperContent content) {
		if (content.getCriterionType() == CriterionType.HEADCOUNT) {
			return content.getPeople();
		}
		return null;
	}

	private static String buildPaperContentText(PaperContent content, List<String> goodsList, Integer peopleValue) {
		String result = "";

		boolean isGoodsSingle = goodsList != null && goodsList.size() == 1;
		boolean isGoodsMultiple = goodsList != null && goodsList.size() > 1;

		// 1. HEADCOUNT + SERVICE + 여러 개 goods
		if (content.getCriterionType() == CriterionType.HEADCOUNT &&
			content.getOptionType() == OptionType.SERVICE &&
			isGoodsMultiple) {
			result = peopleValue + "명 이상 식사 시 " + content.getCategory() + " 제공";
		}
		// 2. HEADCOUNT + SERVICE + 단일 goods
		else if (content.getCriterionType() == CriterionType.HEADCOUNT &&
			content.getOptionType() == OptionType.SERVICE &&
			isGoodsSingle) {
			result = peopleValue + "명 이상 식사 시 " + goodsList.get(0) + " 제공";
		}
		// 3. HEADCOUNT + DISCOUNT
		else if (content.getCriterionType() == CriterionType.HEADCOUNT &&
			content.getOptionType() == OptionType.DISCOUNT) {
			result = peopleValue + "명 이상 식사 시 " + content.getDiscount() + "% 할인";
		}
		// 4. PRICE + SERVICE + 여러 개 goods
		else if (content.getCriterionType() == CriterionType.PRICE &&
			content.getOptionType() == OptionType.SERVICE &&
			isGoodsMultiple) {
			result = content.getCost() + " 이상 주문 시 " + content.getCategory() + " 제공";
		}
		// 5. PRICE + SERVICE + 단일 goods
		else if (content.getCriterionType() == CriterionType.PRICE &&
			content.getOptionType() == OptionType.SERVICE &&
			isGoodsSingle) {
			result = content.getCost() + " 이상 주문 시 " + goodsList.get(0) + " 제공";
		}
		// 6. PRICE + DISCOUNT
		else if (content.getCriterionType() == CriterionType.PRICE &&
			content.getOptionType() == OptionType.DISCOUNT) {
			result = content.getCost() + " 이상 주문 시 " + content.getDiscount() + "% 할인";
		}

		return result;
	}
}
