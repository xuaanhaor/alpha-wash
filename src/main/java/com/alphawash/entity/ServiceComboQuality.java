package com.alphawash.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "service_combo_quality",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_combo_catalog_service",
                        columnNames = {"combo_catalog_code", "service_catalog_code"}
                )
        }
)
public class ServiceComboQuality extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // combo_catalog_code REFERENCES service_combo_catalog(code)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "combo_catalog_code", referencedColumnName = "code", nullable = false)
    private ServiceComboCatalog comboCatalog;

    // service_catalog_code REFERENCES service_catalog(code)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "service_catalog_code", referencedColumnName = "code", nullable = false)
    private ServiceCatalog serviceCatalog;

    @Column(nullable = false)
    private Integer quality;
}