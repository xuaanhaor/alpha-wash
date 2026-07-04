package com.alphawash.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", unique = true, nullable = false)
    private String code;

    @Column(name = "barcode")
    private String barcode;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @ManyToOne
    @JoinColumn(name = "category_code", referencedColumnName = "code")
    private ProductCategory category;

    @Column(name = "brand")
    private String brand;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "cost_price")
    private BigDecimal costPrice;

    @Column(name = "selling_price", nullable = false)
    private BigDecimal sellingPrice;

    @Column(name = "min_price")
    private BigDecimal minPrice;

    @Column(name = "suggested_price")
    private BigDecimal suggestedPrice;

    @Column(name = "current_stock")
    private Integer currentStock = 0;

    @Column(name = "min_stock")
    private Integer minStock = 0;

    @Column(name = "unit")
    private String unit;

    @Column(name = "location")
    private String location;

    @Column(name = "track_inventory")
    private Boolean trackInventory = true;

    @ManyToOne
    @JoinColumn(name = "supplier_code", referencedColumnName = "code")
    private Supplier defaultSupplier;

    @Column(name = "supplier_sku")
    private String supplierSku;

    @Column(name = "is_active")
    private Boolean isActive = true;
}
