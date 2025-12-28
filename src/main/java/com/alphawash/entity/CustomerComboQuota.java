package com.alphawash.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "customer_combo_quota")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerComboQuota extends BaseEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "code", unique = true, nullable = false)
    private String code;

    @ManyToOne
    @JoinColumn(
            name = "customer_combo_summary_code",
            referencedColumnName = "code",
            nullable = false
    )
    private CustomerComboSummary customerComboSummary;

    @ManyToOne
    @JoinColumn(
            name = "service_catalog_code",
            referencedColumnName = "code",
            nullable = false
    )
    private ServiceCatalog serviceCatalog;

    @Column(name = "total_uses", nullable = false)
    private Integer totalUses;

    @Column(name = "used_uses")
    private Integer usedUses;

    @Column(name = "end_at", nullable = false)
    private LocalDateTime endAt;
}
