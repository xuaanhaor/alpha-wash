package com.alphawash.response;

import java.util.List;

public record ProductionResponse(
        Long id,
        String stt,
        String date,
        String timeIn,
        String timeOut,
        String plateNumber,
        String customerName,
        String sdt,
        String carCompany,
        String vehicleLine,
        String service,
        String carSize,
        String status,
        String statusPayment,
        List<String> employees
) {}
