package com.alphawash.request;

import com.alphawash.dto.BrandDto;
import com.alphawash.dto.ModelDto;

import java.util.UUID;

public record VehicleRequest(
        UUID vehicleId,
        UUID customerId,
        String licensePlate,
        BrandDto brand,
        ModelDto model,
        String imageUrl,
        String note) {}
