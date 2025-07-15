package com.alphawash.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "model")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Model extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "model_name")
    private String modelName;

    @Column(name = "size")
    private String size;

    @ManyToOne
    @JoinColumn(name = "brand_code")
    private Brand brand;
}
