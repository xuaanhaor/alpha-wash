package com.alphawash.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "order_service_detail")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderServiceDetail {
    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "order_service_id")
    private OrderService orderService;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;
}
