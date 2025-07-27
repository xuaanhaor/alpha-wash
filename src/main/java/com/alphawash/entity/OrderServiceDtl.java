package com.alphawash.entity;

import jakarta.persistence.*;
import lombok.*;

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
}
