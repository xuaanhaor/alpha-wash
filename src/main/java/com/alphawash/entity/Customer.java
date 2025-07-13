package com.alphawash.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.List;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "customer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "phone", unique = true, nullable = false)
    private String phone;

    @Column(columnDefinition = "TEXT")
    private String note;

    @OneToMany(mappedBy = "customer")
    @JsonManagedReference
    private List<Vehicle> vehicles;
}
