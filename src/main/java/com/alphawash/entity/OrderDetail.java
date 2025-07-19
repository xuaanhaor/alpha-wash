package com.alphawash.entity;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "order_detail")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetail extends BaseEntity {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "employee_id")
    private String employeeId;

    @Column(name = "status")
    private String status;

    @ManyToOne
    @JoinColumn(name = "service_catalog_code", referencedColumnName = "code")
    private ServiceCatalog serviceCatalog;

    @Column(name = "note")
    private String note;

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;
}
