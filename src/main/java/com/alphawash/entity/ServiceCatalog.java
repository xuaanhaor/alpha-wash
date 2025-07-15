package com.alphawash.entity;

import com.alphawash.constant.Size;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.*;

@Entity
@Table(name = "service_catalog")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceCatalog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //    @JdbcTypeCode(SqlTypes.OTHER)
    @Enumerated(EnumType.STRING)
    @Column(name = "size")
    private Size size;

    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "service_id")
    private Service service;
}
