package com.assu.server.domain.partnership.converter;

import com.assu.server.domain.admin.entity.Admin;
import com.assu.server.domain.common.enums.ActivationStatus;
import com.assu.server.domain.partner.entity.Partner;
import com.assu.server.domain.partnership.dto.PartnershipRequestDTO;
import com.assu.server.domain.partnership.dto.PartnershipResponseDTO;
import com.assu.server.domain.partnership.entity.Goods;
import com.assu.server.domain.partnership.entity.Paper;
import com.assu.server.domain.partnership.entity.PaperContent;
import com.assu.server.domain.store.entity.Store;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PartnershipConverter {

    public static Paper toPaperEntity(
            PartnershipRequestDTO.WritePartnershipRequestDTO partnershipRequestDTO,
            Admin admin,
            Partner partner,
            Store store

    ) {
        return Paper.builder()
                .partnershipPeriodStart(partnershipRequestDTO.getPartnershipPeriodStart())
                .partnershipPeriodEnd(partnershipRequestDTO.getPartnershipPeriodEnd())
                .isActivated(ActivationStatus.SUSPEND)
                .admin(admin)
                .store(store)
                .partner(partner)
                .build();
    }

    public static List<PaperContent> toPaperContents(
            PartnershipRequestDTO.WritePartnershipRequestDTO partnershipRequestDTO,
            Paper paper
    ) {
        if (partnershipRequestDTO.getOptions() == null || partnershipRequestDTO.getOptions().isEmpty()) {
            return Collections.emptyList();
        }
        List<PaperContent> contents = new ArrayList<>(partnershipRequestDTO.getOptions().size());
        for (var o : partnershipRequestDTO.getOptions()) {
            PaperContent content = PaperContent.builder()
                    .paper(paper)
                    .criterionType(o.getCriterionType())
                    .optionType(o.getOptionType())
                    .people(o.getPeople())
                    .cost(o.getCost())
                    .category(o.getCategory())
                    .discount(o.getDiscountRate())
                    .build();
            contents.add(content);
        }
        return contents;
    }

    public static List<List<Goods>> toGoodsBatches(
            PartnershipRequestDTO.WritePartnershipRequestDTO partnershipRequestDTO
    ) {
        if (partnershipRequestDTO == null || partnershipRequestDTO.getOptions().isEmpty()) {
            return List.of();
        }
        List<List<Goods>> batches = new ArrayList<>(partnershipRequestDTO.getOptions().size());
        for (var o : partnershipRequestDTO.getOptions()) {
            if (o.getGoods() == null || o.getGoods().isEmpty()) {
                batches.add(List.of());
                continue;
            }
            List<Goods> goodsList = o.getGoods().stream()
                    .map(g -> Goods.builder()
                            .belonging(g.getGoodsName())
                            .build())
                    .collect(Collectors.toList());
            batches.add(goodsList);
        }
        return batches;
    }

    public static PartnershipResponseDTO.WritePartnershipResponseDTO writePartnershipResultDTO(
            Paper paper,
            List<PaperContent> contents,
            List<List<Goods>> goodsBatches
    ) {
        List<PartnershipResponseDTO.PartnershipOptionResponseDTO> optionDTOS = new ArrayList<>();
        int n = contents == null ? 0 : contents.size();
        for(int i = 0;i < n;i++){
            PaperContent pc = contents.get(i);
            List<Goods> goods = (goodsBatches != null && goodsBatches.size() > i)
                    ? goodsBatches.get(i) : List.of();
            optionDTOS.add(optionResultDTO(pc, goods));
        }

        return PartnershipResponseDTO.WritePartnershipResponseDTO.builder()
                .partnershipId(paper.getId())
                .partnershipPeriodStart(paper.getPartnershipPeriodStart())
                .partnershipPeriodEnd(paper.getPartnershipPeriodEnd())
                .adminId(paper.getAdmin() == null ? null : paper.getAdmin().getId())
                .partnerId(paper.getStore() == null ? null : paper.getPartner().getId())
                .storeId(paper.getStore() == null ? null : paper.getStore().getId())
                .options(optionDTOS)
                .build();
    }

    public static PartnershipResponseDTO.PartnershipOptionResponseDTO optionResultDTO(
            PaperContent pc, List<Goods> goods
    ) {
        return PartnershipResponseDTO.PartnershipOptionResponseDTO.builder()
                .optionType(pc.getOptionType())
                .criterionType(pc.getCriterionType())
                .people(pc.getPeople())
                .cost(pc.getCost())
                .category(pc.getCategory())
                .discountRate(pc.getDiscount())
                .goods(goodsResultDTO(goods))
                .build();
    }

    public static List<PartnershipResponseDTO.PartnershipGoodsResponseDTO> goodsResultDTO(List<Goods> goods) {
        if(goods == null || goods.isEmpty()) return List.of();
        return goods.stream()
                .map(g -> PartnershipResponseDTO.PartnershipGoodsResponseDTO.builder()
                        .goodsId(g.getId())
                        .goodsName(g.getBelonging())
                        .build())
                .collect(Collectors.toList());
    }
}
