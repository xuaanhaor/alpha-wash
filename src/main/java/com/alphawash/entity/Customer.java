package com.alphawash.entity;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "customer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer extends BaseEntity {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "phone", unique = true)
    private String phone;

    @Column(columnDefinition = "TEXT")
    private String note;
}
