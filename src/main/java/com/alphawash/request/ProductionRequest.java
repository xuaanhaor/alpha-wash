package com.alphawash.request;

import java.time.LocalDateTime;

public record ProductionRequest(
        LocalDateTime dateIn,
        LocalDateTime dateOut,
        String plate,
        String customerName,
        String phone,
        String carBrand,
        String carModel,
        String carService,
        String employee,
        String note,
        LocalDateTime updateTime) {}
