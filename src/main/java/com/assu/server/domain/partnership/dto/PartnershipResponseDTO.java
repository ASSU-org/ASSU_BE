package com.assu.server.domain.partnership.dto;

import com.assu.server.domain.partnership.entity.enums.CriterionType;
import com.assu.server.domain.partnership.entity.enums.OptionType;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class PartnershipResponseDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class WritePartnershipResponseDTO {
        private Long partnershipId;
        private LocalDate partnershipPeriodStart;
        private LocalDate partnershipPeriodEnd;
        private Long adminId;
        private Long partnerId;
        private Long storeId;
        private List<PartnershipOptionResponseDTO> options;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PartnershipOptionResponseDTO {
        private OptionType optionType;
        private CriterionType criterionType;
        private Integer people;
        private Long cost;
        private String category;
        private Long discountRate;

        private List<PartnershipGoodsResponseDTO> goods;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PartnershipGoodsResponseDTO {
        private Long goodsId;
        private String goodsName;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateResponseDTO {
        private Long partnershipId;
        private String prevStatus;
        private String newStatus;
        private LocalDateTime changedAt;
    }
}
