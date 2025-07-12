package com.alphawash.dto;

import com.alphawash.entity.Brand;
import com.alphawash.entity.Customer;
import com.alphawash.entity.Model;
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
    private Brand brand;
    private Model model;
    private String imageUrl;
    private String note;
}
