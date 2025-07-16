package com.alphawash.entity;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "vehicle")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle extends BaseEntity {
    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(name = "license_plate", unique = true, nullable = false)
    private String licensePlate;

    @ManyToOne
    @JoinColumn(name = "brand_code", referencedColumnName = "code")
    private Brand brand;

    @ManyToOne
    @JoinColumn(name = "model_code", referencedColumnName = "code")
    private Model model;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(columnDefinition = "TEXT")
    private String note;
}
