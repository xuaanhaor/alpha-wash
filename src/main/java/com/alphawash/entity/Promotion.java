package com.alphawash.entity;

import com.alphawash.constant.PromoType;
import com.alphawash.constant.PromotionMethod;
import com.alphawash.constant.PromotionStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "promotion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Promotion extends BaseEntity {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "promo_code", length = 50, nullable = false, unique = true)
    private String promoCode;

    @Column(name = "promo_name", length = 255, nullable = false)
    private String promoName;

    @Enumerated(EnumType.STRING)
    @Column(name = "promo_type", length = 30, nullable = false)
    private PromoType promoType;

    @Column(name = "value", precision = 10, scale = 2, nullable = false)
    private BigDecimal value;

    @Column(name = "usage_limit")
    private Integer usageLimit;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private PromotionStatus status = PromotionStatus.DRAFT;

    @Enumerated(EnumType.STRING)
    @Column(name = "promotion_method", length = 50, nullable = false)
    private PromotionMethod promotionMethod;

    @Column(name = "campaign_link")
    private String campaignLink;

    @Column(name = "target_audience")
    private String targetAudience;

}
