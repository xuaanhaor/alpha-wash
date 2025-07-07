package com.alphawash.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "production")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Production {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dateIn;

    private LocalDateTime dateOut;

    private String plate;

    private String customerName;

    private String phone;

    private String carBrand;

    private String carModel;

    private String carService;

    private String employee;

    private String note;

    private LocalDateTime updateTime;
}
