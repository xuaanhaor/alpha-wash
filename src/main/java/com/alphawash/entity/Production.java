package com.alphawash.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

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
    private String stt;
    private String date;
    private String timeIn;
    private String timeOut;
    private String plateNumber;
    private String customerName;
    private String sdt;
    private String carCompany;
    private String vehicleLine;
    private String service;
    private String carSize;
    private String status;
    private String statusPayment;
    private String employees;
}
