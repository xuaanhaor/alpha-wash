package com.alphawash.request;

import com.alphawash.dto.BrandDto;
import com.alphawash.dto.CustomerDto;
import com.alphawash.dto.ModelDto;
import java.util.UUID;

public record VehicleRequest(
        UUID id,
        CustomerDto customer,
        String licensePlate,
        BrandDto brand,
        ModelDto model,
        String imageUrl,
        String note) {}
