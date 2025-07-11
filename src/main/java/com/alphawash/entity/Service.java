package com.alphawash.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "service")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "service_name")
    private String serviceName;

    private String duration;

    private String note;

    @ManyToOne
    @JoinColumn(name = "service_type_id")
    private ServiceType serviceType;
}
