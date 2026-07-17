package com.alphawash.exception;

import com.alphawash.dto.VehicleDto;
import lombok.Getter;

@Getter
public class DuplicateVehicleException extends RuntimeException {
    private final VehicleDto existingVehicle;

    public DuplicateVehicleException(String message, VehicleDto existingVehicle) {
        super(message);
        this.existingVehicle = existingVehicle;
    }
}
