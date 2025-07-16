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
public class Service extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", unique = true, nullable = false)
    private String code;

    @Column(name = "service_name")
    private String serviceName;

    @Column(name = "duration")
    private String duration;

    @Column(name = "note")
    private String note;

    @ManyToOne
    @JoinColumn(name = "service_type_code")
    private ServiceType serviceType;
}
