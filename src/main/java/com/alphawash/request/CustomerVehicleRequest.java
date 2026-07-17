package com.alphawash.request;

import java.util.UUID;

public record CustomerVehicleRequest(
        String licensePlate, Long brandId, Long modelId, String note, UUID linkVehicleId) {}
