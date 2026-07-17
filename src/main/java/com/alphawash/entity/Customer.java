package com.alphawash.entity;

import com.alphawash.constant.CustomerGender;
import com.alphawash.constant.CustomerStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Column(name = "email")
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 10)
    private CustomerGender gender;

    @Column(name = "birthday")
    private LocalDate birthday;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    @Builder.Default
    private CustomerStatus status = CustomerStatus.ACTIVE;
}
