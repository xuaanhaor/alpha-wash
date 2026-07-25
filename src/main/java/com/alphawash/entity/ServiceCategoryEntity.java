package com.alphawash.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "service_category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceCategoryEntity extends BaseEntity {

    @Id
    @GeneratedValue
    private UUID id;

    /** Code dùng để backward-compat với enum cũ: WASHING, INTERIOR, ... */
    @Column(name = "code", unique = true, nullable = false)
    private String code;

    /** Tên hiển thị: Rửa xe, Nội thất, ... */
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "sort_order", nullable = false)
    @Builder.Default
    private int sortOrder = 0;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private boolean active = true;
}
