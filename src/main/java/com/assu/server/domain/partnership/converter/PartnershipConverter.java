package com.assu.server.domain.partnership.converter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import com.assu.server.domain.common.entity.BaseEntity;
import com.assu.server.domain.partnership.dto.PaperContentResponseDTO;
import com.assu.server.domain.admin.entity.Admin;
import com.assu.server.domain.common.enums.ActivationStatus;
import com.assu.server.domain.partner.entity.Partner;
import com.assu.server.domain.partnership.dto.PartnershipRequestDTO;
import com.assu.server.domain.partnership.dto.PartnershipResponseDTO;
import com.assu.server.domain.partnership.entity.Goods;
import com.assu.server.domain.partnership.entity.Paper;
import com.assu.server.domain.partnership.entity.PaperContent;
import com.assu.server.domain.partnership.entity.enums.CriterionType;
import com.assu.server.domain.partnership.entity.enums.OptionType;
import com.assu.server.domain.user.entity.PartnershipUsage;
import com.assu.server.domain.user.entity.Student;
import com.assu.server.domain.store.entity.Store;

public class PartnershipConverter {

	public static PartnershipUsage toPartnershipUsage(PartnershipRequestDTO.finalRequest dto, Student student, Long paperId) {
		return PartnershipUsage.builder()
			.adminName(dto.getAdminName())
			.date(LocalDate.now())
			.place(dto.getPlaceName())
			.student(student)
			.paperId(paperId)
			.isReviewed(false)
			.contentId(dto.getContentId())
			.partnershipContent(dto.getPartnershipContent())
			.build();
	}

	public static Paper toDraftPaperEntity(Admin admin, Partner partner, Store store) {
		return Paper.builder()
				.admin(admin)
				.partner(partner)
				.store(store)
				.partnershipPeriodStart(null)
				.partnershipPeriodEnd(null)
				.isActivated(ActivationStatus.BLANK)
				.contractImageKey(null)
				.build();
	}

    public static List<PaperContent> toPaperContents(
            PartnershipRequestDTO.WritePartnershipRequestDTO partnershipRequestDTO,
            Paper paper
    ) {
        if (partnershipRequestDTO.getOptions() == null || partnershipRequestDTO.getOptions().isEmpty()) {
            return Collections.emptyList();
        }
		return partnershipRequestDTO.getOptions().stream()
				.map(optionDto -> PaperContent.builder()
						.paper(paper) // 어떤 Paper에 속하는지 연결
						.optionType(optionDto.getOptionType())
						.criterionType(optionDto.getCriterionType())
						.people(optionDto.getPeople())
						.cost(optionDto.getCost())
						.category(optionDto.getCategory())
						.discount(optionDto.getDiscountRate()) // DTO의 discountRate를 Entity의 discount에 매핑
						.build())
				.toList();
    }




    public static List<List<Goods>> toGoodsBatches(
            PartnershipRequestDTO.WritePartnershipRequestDTO partnershipRequestDTO
    ) {
        if (partnershipRequestDTO == null || partnershipRequestDTO.getOptions().isEmpty()) {
            return Collections.emptyList();
        }
        return partnershipRequestDTO.getOptions().stream()
				.map(optionDto -> {
					if (optionDto.getGoods() == null || optionDto.getGoods().isEmpty()) {
						return Collections.<Goods>emptyList();
					}
					return optionDto.getGoods().stream()
							.map(goodsDto -> Goods.builder()
									.belonging(goodsDto.getGoodsName()) // DTO의 goodsName을 엔티티의 belonging에 매핑
									.build())
							.toList();
				})
				.toList();
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
			.adminId(content.getPaper().getAdmin().getId())
			.adminName(content.getPaper().getAdmin().getName())
			.cost(content.getCost())
			.paperContent(paperContentText)
			.contentId(content.getId())
			.goods(goodsList)
			.people(peopleValue)
			.build();
	}


	private static List<String> extractGoods(PaperContent content) {
		if (content.getOptionType() == OptionType.SERVICE ) {
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
			result = content.getCost() + "원 이상 주문 시 " + content.getCategory() + " 제공";
		}
		// 5. PRICE + SERVICE + 단일 goods
		else if (content.getCriterionType() == CriterionType.PRICE &&
			content.getOptionType() == OptionType.SERVICE &&
			isGoodsSingle) {
			result = content.getCost() + "원 이상 주문 시 " + goodsList.get(0) + " 제공";
		}
		// 6. PRICE + DISCOUNT
		else if (content.getCriterionType() == CriterionType.PRICE &&
			content.getOptionType() == OptionType.DISCOUNT) {
			result = content.getCost() + "원 이상 주문 시 " + content.getDiscount() + "% 할인";
		}

		return result;
	}


	public static Paper toPaperForManual(
		Admin admin, Store store,
		LocalDate start, LocalDate end,
		ActivationStatus status
	) {
		return Paper.builder()
			.admin(admin)
			.store(store)
			.partner(null)
			.isActivated(status)
			.partnershipPeriodStart(start)
			.partnershipPeriodEnd(end)
			.build();
	}


    public static List<PaperContent> toPaperContentsForManual(
            List<PartnershipRequestDTO.PartnershipOptionRequestDTO> options,
            Paper paper
    ) {
        if (options == null || options.isEmpty()) return List.of();
        List<PaperContent> list = new ArrayList<>(options.size());
        for (var o : options) {
            list.add(PaperContent.builder()
                    .paper(paper)
                    .optionType(o.getOptionType())
                    .criterionType(o.getCriterionType())
                    .people(o.getPeople())
                    .cost(o.getCost())
                    .category(o.getCategory())
                    .discount(o.getDiscountRate())
                    .build());
        }
        return list;
    }

    public static List<Goods> toGoodsForContent(
            PartnershipRequestDTO.PartnershipOptionRequestDTO option,
            PaperContent content
    ) {
        if (option.getGoods() == null || option.getGoods().isEmpty()) return List.of();
        List<Goods> batch = new ArrayList<>(option.getGoods().size());
        for (var g : option.getGoods()) {
            batch.add(Goods.builder()
                    .content(content)
                    .belonging(g.getGoodsName())
                    .build());
        }
        return batch;
    }


    public static PartnershipResponseDTO.WritePartnershipResponseDTO writePartnershipResultDTO(
            Paper paper,
            List<PaperContent> contents,
            List<List<Goods>> goodsBatches
    ) {
        List<PartnershipResponseDTO.PartnershipOptionResponseDTO> optionDTOS = new ArrayList<>();
        if (contents != null) {
            for (int i = 0; i < contents.size(); i++) {
                PaperContent pc = contents.get(i);
                List<Goods> goods = (goodsBatches != null && goodsBatches.size() > i)
                        ? goodsBatches.get(i) : List.of();
                optionDTOS.add(
                        PartnershipResponseDTO.PartnershipOptionResponseDTO.builder()
                                .optionType(pc.getOptionType())
                                .criterionType(pc.getCriterionType())
                                .people(pc.getPeople())
                                .cost(pc.getCost())
                                .category(pc.getCategory())
                                .discountRate(pc.getDiscount())
                                .goods(goodsResultDTO(goods))
                                .build()
                );
            }
        }
        return PartnershipResponseDTO.WritePartnershipResponseDTO.builder()
                .partnershipId(paper.getId())
                .partnershipPeriodStart(paper.getPartnershipPeriodStart())
                .partnershipPeriodEnd(paper.getPartnershipPeriodEnd())
                .adminId(paper.getAdmin()    != null ? paper.getAdmin().getId()     : null)
                .partnerId(paper.getPartner()!= null ? paper.getPartner().getId()   : null) // 수동등록이면 null
                .storeId(paper.getStore()    != null ? paper.getStore().getId()     : null)
                .storeName(paper.getStore().getName())
                .adminName(paper.getAdmin().getName())
				.isActivated(paper.getIsActivated())
                .options(optionDTOS)
                .build();
    }

    public static List<PartnershipResponseDTO.PartnershipGoodsResponseDTO> goodsResultDTO(List<Goods> goods) {
        if (goods == null || goods.isEmpty()) return List.of();
        return goods.stream()
                .map(g -> PartnershipResponseDTO.PartnershipGoodsResponseDTO.builder()
                        .goodsId(g.getId())
                        .goodsName(g.getBelonging())
                        .build())
                .toList();
    }

	public static PartnershipResponseDTO.CreateDraftResponseDTO toCreateDraftResponseDTO(Paper paper) {
		return PartnershipResponseDTO.CreateDraftResponseDTO.builder()
				.paperId(paper.getId())
				.build();
	}

	public static void updatePaperFromDto(Paper paper, PartnershipRequestDTO.WritePartnershipRequestDTO dto) {
		paper.setPartnershipPeriodStart(dto.getPartnershipPeriodStart());
		paper.setPartnershipPeriodEnd(dto.getPartnershipPeriodEnd());
		paper.setIsActivated(ActivationStatus.SUSPEND);
	}

	public static PartnershipResponseDTO.GetPartnershipDetailResponseDTO getPartnershipResultDTO(
			Paper paper,
			List<PaperContent> contents,
			List<List<Goods>> goodsBatches
	) {
		List<LocalDateTime> allTimestamps = new ArrayList<>();

		if (paper.getUpdatedAt() != null) allTimestamps.add(paper.getUpdatedAt());
		if (contents != null) {
			contents.stream()
					.map(BaseEntity::getUpdatedAt)
					.filter(Objects::nonNull)
					.forEach(allTimestamps::add);
		}
		if (goodsBatches != null) {
			goodsBatches.stream()
					.flatMap(List::stream)
					.map(BaseEntity::getUpdatedAt)
					.filter(Objects::nonNull)
					.forEach(allTimestamps::add);
		}

		LocalDateTime mostRecentUpdatedAt = allTimestamps.stream()
				.max(Comparator.naturalOrder())
				.orElse(paper.getUpdatedAt());

		List<PartnershipResponseDTO.PartnershipOptionResponseDTO> optionDTOS = new ArrayList<>();
		if (contents != null) {
			for (int i = 0; i < contents.size(); i++) {
				PaperContent pc = contents.get(i);
				List<Goods> goods = (goodsBatches != null && goodsBatches.size() > i)
						? goodsBatches.get(i) : List.of();
				optionDTOS.add(
						PartnershipResponseDTO.PartnershipOptionResponseDTO.builder()
								.optionType(pc.getOptionType())
								.criterionType(pc.getCriterionType())
								.people(pc.getPeople())
								.cost(pc.getCost())
								.category(pc.getCategory())
								.discountRate(pc.getDiscount())
								.goods(goodsResultDTO(goods))
								.build()
				);
			}
		}

		return PartnershipResponseDTO.GetPartnershipDetailResponseDTO.builder()
				.partnershipId(paper.getId())
				.updatedAt(mostRecentUpdatedAt) // 가장 최근 UpdatedAt 값 가져오기
				.partnershipPeriodStart(paper.getPartnershipPeriodStart())
				.partnershipPeriodEnd(paper.getPartnershipPeriodEnd())
				.adminId(paper.getAdmin()    != null ? paper.getAdmin().getId()     : null)
				.partnerId(paper.getPartner()!= null ? paper.getPartner().getId()   : null) // 수동등록이면 null
				.storeId(paper.getStore()    != null ? paper.getStore().getId()     : null)
				.options(optionDTOS)
				.build();
	}
}
