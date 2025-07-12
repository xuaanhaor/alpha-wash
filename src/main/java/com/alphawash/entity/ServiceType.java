package com.alphawash.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "service_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "service_type_name")
    private String serviceTypeName;
}
