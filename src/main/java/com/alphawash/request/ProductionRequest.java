package com.alphawash.request;

import java.time.LocalDateTime;

public record ProductionRequest(
        String stt,
        String date,
        String timeIn,
        String timeOut,
        String plateNumber,
        String user,
        String sdt,
        String carCompany,
        String vehicleLine,
        String service,
        String status
) {}
