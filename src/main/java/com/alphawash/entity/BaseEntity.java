package com.alphawash.entity;

import com.alphawash.constant.Constant;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

/**
 * Hạn chế sửa đổi các trường trong BaseEntity
 *
 */
@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BaseEntity {
    @Column(name = "delete_flag", nullable = false)
    private Boolean deleteFlag;

    @Column(name = "created_by", updatable = false)
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    @Column(name = "exclusive_key")
    private Integer exclusiveKey;

    @PrePersist
    protected void onCreate() {
        this.createdBy = Constant.ADMINISTRATOR;
        this.createdAt = LocalDateTime.now();
        this.deleteFlag = false;
        this.exclusiveKey = Constant.ZERO;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedBy = Constant.ADMINISTRATOR;
        this.exclusiveKey++;
        this.updatedAt = LocalDateTime.now();
    }
}
