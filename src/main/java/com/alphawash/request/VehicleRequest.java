package com.alphawash.request;

import com.alphawash.dto.*;
import java.util.UUID;

public record VehicleRequest(
        UUID id,
        CustomerDto customer,
        String licensePlate,
        BrandDto brand,
        ModelFromEntityDto model,
        String imageUrl,
        String note) {}
