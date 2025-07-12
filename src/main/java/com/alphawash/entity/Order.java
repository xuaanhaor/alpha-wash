package com.alphawash.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "employee_id")
    private String employeeId;

    private LocalDateTime date;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "checkin_time")
    private LocalTime checkinTime;

    @Column(name = "checkout_time")
    private LocalTime checkoutTime;

    private BigDecimal vat;

    @Column(name = "total_price")
    private BigDecimal totalPrice;
}
