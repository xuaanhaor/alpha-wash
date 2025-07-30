package com.alphawash.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "employee")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "phone", unique = true, nullable = false)
    private String phone;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "bank_account")
    private String bankAccount;

    @Column(name = "date_of_birth")
    private LocalDateTime dateOfBirth;

    @Column(name = "identity_number")
    private String identityNumber;

    @Column(name = "join_date")
    private LocalDateTime joinDate;

    @Column(name = "work_status")
    private String workStatus;

    @Column(columnDefinition = "TEXT")
    private String note;
}
