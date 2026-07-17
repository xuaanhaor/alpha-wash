package com.alphawash.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "merge_vehicle_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MergeVehicleLog extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "merge_date")
    private LocalDateTime mergeDate;

    @Column(name = "operator_username")
    private String operatorUsername;

    @Column(name = "primary_customer_id")
    private UUID primaryCustomerId;

    @Column(name = "primary_customer_name")
    private String primaryCustomerName;

    @Column(name = "primary_vehicle_id")
    private UUID primaryVehicleId;

    @Column(name = "primary_vehicle_license_plate")
    private String primaryVehicleLicensePlate;

    @Column(name = "duplicate_customer_ids", columnDefinition = "TEXT")
    private String duplicateCustomerIds;

    @Column(name = "duplicate_customer_names", columnDefinition = "TEXT")
    private String duplicateCustomerNames;

    @Column(name = "duplicate_vehicle_license_plates", columnDefinition = "TEXT")
    private String duplicateVehicleLicensePlates;

    @Column(name = "orders_migrated")
    private Integer ordersMigrated;

    @Column(name = "order_details_migrated")
    private Integer orderDetailsMigrated;

    @Column(name = "customers_archived")
    private Integer customersArchived;

    @Column(name = "status")
    private String status;
}
