package com.alphawash.entity;

import com.alphawash.constant.ServiceCategory;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "service_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceItem extends BaseEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private ServiceCategory category;

    /** Thương hiệu: STEK, 3M, null */
    @Column(name = "brand")
    private String brand;

    /** Loại film/sản phẩm: DYNO SHIELD, CRYSTALLINE, null */
    @Column(name = "type_detail")
    private String typeDetail;

    /** Thời hạn bảo hành: "5 năm", "10 năm", null */
    @Column(name = "warranty")
    private String warranty;

    /** Giá xe nhỏ */
    @Column(name = "price_s", precision = 18, scale = 2)
    private BigDecimal priceS;

    /** Giá xe vừa */
    @Column(name = "price_m", precision = 18, scale = 2)
    private BigDecimal priceM;

    /** Giá xe lớn / SUV */
    @Column(name = "price_l", precision = 18, scale = 2)
    private BigDecimal priceL;

    /** Giá xe Sedan (cho dịch vụ phân Sedan/SUV) */
    @Column(name = "price_sedan", precision = 18, scale = 2)
    private BigDecimal priceSEDAN;

    /** Giá xe SUV */
    @Column(name = "price_suv", precision = 18, scale = 2)
    private BigDecimal priceSUV;

    /** Giá xe quá khổ / đặc biệt */
    @Column(name = "price_oversize", precision = 18, scale = 2)
    private BigDecimal priceOverSize;

    /** Có thể là dịch vụ tặng kèm không */
    @Column(name = "can_be_bonus", nullable = false)
    @Builder.Default
    private boolean canBeBonus = false;

    /** Hiển thị / ẩn trong bảng giá */
    @Column(name = "active", nullable = false)
    @Builder.Default
    private boolean active = true;

    /** Mô tả thêm */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /** Thứ tự hiển thị trong category */
    @Column(name = "sort_order", nullable = false)
    @Builder.Default
    private int sortOrder = 0;
}
