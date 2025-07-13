package com.alphawash.dto;

import com.alphawash.entity.Customer;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleDto {
    private UUID id;
    private Customer customer;
    private String licensePlate;
    private BrandDto brand;
    private ModelFromEntityDto model;
    private String imageUrl;
    private String note;
}
