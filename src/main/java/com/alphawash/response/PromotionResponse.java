package com.alphawash.response;

import com.alphawash.constant.PromoType;
import com.alphawash.constant.PromotionMethod;
import com.alphawash.constant.PromotionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PromotionResponse {
    UUID id;
    String promoCode;
    String promoName;
    PromoType promoType;
    BigDecimal value;
    Integer usageLimit;
    LocalDateTime startDate;
    LocalDateTime endDate;
    String description;
    PromotionStatus status;
    PromotionMethod promotionMethod;
    String campaignLink;
    String targetAudience;
    String createdBy;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    List<AddPromotionServicesResponse> services;
}
