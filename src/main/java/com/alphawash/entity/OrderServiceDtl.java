package com.alphawash.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "order_service_dtl",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"order_detail_code", "service_catalog_code"})})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderServiceDtl {

    @Id
    @Column(name = "code", unique = true, nullable = false)
    private String code;

    @ManyToOne
    @JoinColumn(name = "order_detail_code", referencedColumnName = "code", nullable = false)
    private OrderDetail orderDetail;

    @Column(name = "service_catalog_code", nullable = false)
    private String serviceCatalogCode;

    @Column(name = "adjusted_price_reason")
    private String adjustedPriceReason;

    @Column(name = "adjusted_price", precision = 18, scale = 2)
    private BigDecimal adjustedPrice;

    @Column(name = "adjusted_price_flag")
    private Boolean adjustedPriceFlag;

    @Column(name = "quantity", nullable = false)
    @Builder.Default
    private Integer quantity = 1;
}
