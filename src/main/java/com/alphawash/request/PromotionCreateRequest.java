package com.alphawash.request;

import com.alphawash.constant.PromoType;
import com.alphawash.constant.PromotionMethod;
import com.alphawash.constant.PromotionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PromotionCreateRequest (
        String promoCode,
        String promoName,
        PromoType promoType,
        BigDecimal value,
        Integer usageLimit,
        LocalDateTime startDate,
        LocalDateTime endDate,
        String description,
        PromotionStatus status,
        PromotionMethod promotionMethod,
        String campaignLink,
        String targetAudience
) {
}
