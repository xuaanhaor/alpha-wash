package com.alphawash.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "customer_combo_summary")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerComboSummary extends BaseEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "code", unique = true, nullable = false)
    private String code;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "order_code", length = 50, nullable = false)
    private String orderCode;

    @Column(name = "service_combo_catalog_code", nullable = false)
    private String serviceComboCatalogCode;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "end_at", nullable = false)
    private LocalDateTime endAt;

    @Column(name = "total_uses", nullable = false)
    private Integer totalUses;

    @Column(name = "used_uses")
    private Integer usedUses;

    @Column(name = "status")
    private String status;

}