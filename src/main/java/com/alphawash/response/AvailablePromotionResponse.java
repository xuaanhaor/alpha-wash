package com.alphawash.response;

import com.alphawash.constant.PromoType;
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
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvailablePromotionResponse {
    private UUID promoId;
    private String promoCode;
    private String promoName;
    private PromoType promoType;
    private BigDecimal value;
    private List<AddPromotionServicesResponse> services;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private boolean usable;
    private String reason;
}
