package com.assu.server.domain.partnership.dto;
import java.util.List;
import lombok.Getter;

import com.assu.server.domain.partnership.entity.enums.CriterionType;
import com.assu.server.domain.partnership.entity.enums.OptionType;
import lombok.*;

import java.time.LocalDate;

public class PartnershipRequestDTO {
    @Getter
    public static class finalRequest{
        String placeName;
        String partnershipContent;
        Long contentId;
        Long discount;
        List<Long> userIds;
    }
    @Getter
    public static class WritePartnershipRequestDTO {
        private Long adminId; // 제안 학생회 아이디
        private LocalDate partnershipPeriodStart;
        private LocalDate partnershipPeriodEnd;
        private List<PartnershipOptionRequestDTO> options; // 동적으로 받는 제안 항목
    }

    @Getter
    public static class PartnershipOptionRequestDTO {
        private OptionType optionType; // 제공 서비스 종류 (서비스 제공, 할인)
        private CriterionType criterionType; // 서비스 제공 기준 (금액, 인원)
        private Integer people;
        private Long cost;
        private String category;
        private Long discountRate;
        private List<PartnershipGoodsRequestDTO> goods; // 서비스 제공 항목

    }

    @Getter
    public static class PartnershipGoodsRequestDTO {
        private String goodsName;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class UpdateRequestDTO {
        private String status;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ManualPartnershipRequestDTO {
        private String storeName;
        private String storeAddress;
        private String storeDetailAddress;
        private LocalDate partnershipPeriodStart;
        private LocalDate partnershipPeriodEnd;
        private List<PartnershipOptionRequestDTO> options;
    }
}
