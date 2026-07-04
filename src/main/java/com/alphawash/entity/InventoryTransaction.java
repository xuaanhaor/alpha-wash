package com.alphawash.entity;

import com.alphawash.constant.InventoryTransactionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "inventory_transaction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryTransaction extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", unique = true, nullable = false)
    private String code;

    @ManyToOne
    @JoinColumn(name = "product_code", referencedColumnName = "code")
    private Product product;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "before_qty", nullable = false)
    private Integer beforeQty;

    @Column(name = "after_qty", nullable = false)
    private Integer afterQty;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private InventoryTransactionType type;

    @Column(name = "reference_number")
    private String referenceNumber;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
