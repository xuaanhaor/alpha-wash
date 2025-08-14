package com.alphawash.entity;

import com.alphawash.constant.Size;
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

    @Column(name = "code", unique = true, nullable = false)
    private String code;

    @Column(name = "model_name")
    private String modelName;

    @Enumerated(EnumType.STRING)
    @Column(name = "size")
    private Size size;

    @Column(name = "note")
    private String note;

    @ManyToOne
    @JoinColumn(name = "brand_code", referencedColumnName = "code")
    private Brand brand;
}
