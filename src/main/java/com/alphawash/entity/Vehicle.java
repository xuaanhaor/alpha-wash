package com.alphawash.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "vehicle")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {
    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(name = "licence_plate", unique = true, nullable = false)
    private String licencePlate;

    @ManyToOne
    @JoinColumn(name = "catalog_id")
    private VehicleCatalog catalog;

    @Column(name = "image_url")
    private String imageUrl;
}
