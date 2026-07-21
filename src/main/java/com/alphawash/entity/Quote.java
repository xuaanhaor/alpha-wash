package com.alphawash.entity;

import com.alphawash.constant.QuoteStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "quote")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Quote extends BaseEntity {

    @Id
    @GeneratedValue
    private UUID id;

    /** Mã báo giá: QT-YYYYMMDD-NNN */
    @Column(name = "quote_code", unique = true, nullable = false, updatable = false)
    private String quoteCode;

    /** Khách hàng (nullable cho khách vãng lai) */
    @Column(name = "customer_id")
    private UUID customerId;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "customer_phone")
    private String customerPhone;

    /** Xe (nullable) */
    @Column(name = "vehicle_id")
    private UUID vehicleId;

    @Column(name = "license_plate")
    private String licensePlate;

    @Column(name = "car_model")
    private String carModel;

    /** S, M, L, SEDAN, SUV, SUV_FULL_SIZE */
    @Column(name = "car_size")
    private String carSize;

    @Column(name = "quote_date")
    private LocalDate quoteDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private QuoteStatus status = QuoteStatus.DRAFT;

    @Column(name = "subtotal", precision = 18, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "discount", precision = 18, scale = 2)
    @Builder.Default
    private BigDecimal discount = BigDecimal.ZERO;

    @Column(name = "extra_charge", precision = 18, scale = 2)
    @Builder.Default
    private BigDecimal extraCharge = BigDecimal.ZERO;

    @Column(name = "total", precision = 18, scale = 2)
    private BigDecimal total;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @OneToMany(mappedBy = "quote", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    @Builder.Default
    private List<QuoteItem> items = new ArrayList<>();
}
