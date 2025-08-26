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

import java.time.LocalDate;
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
}
