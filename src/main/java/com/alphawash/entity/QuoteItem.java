package com.alphawash.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "quote_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuoteItem {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_id", nullable = false)
    private Quote quote;

    /** Tham chiếu dịch vụ (nullable nếu là item tự do) */
    @Column(name = "service_id")
    private UUID serviceId;

    /** Snapshot tên dịch vụ tại thời điểm tạo báo giá */
    @Column(name = "service_name", nullable = false)
    private String serviceName;

    @Column(name = "brand")
    private String brand;

    @Column(name = "type_detail")
    private String typeDetail;

    @Column(name = "warranty")
    private String warranty;

    /** Snapshot giá – không thay đổi khi service thay đổi sau này */
    @Column(name = "price", nullable = false, precision = 18, scale = 2)
    private BigDecimal price;

    /** Dịch vụ tặng kèm */
    @Column(name = "is_bonus", nullable = false)
    @Builder.Default
    private boolean bonus = false;

    @Column(name = "sort_order", nullable = false)
    @Builder.Default
    private int sortOrder = 0;
}
