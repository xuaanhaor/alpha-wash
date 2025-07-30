package com.alphawash.request;

import java.time.LocalDateTime;

public record EmployeeRequest(
        String name,
        String phone,
        String bankName,
        String bankAccount,
        LocalDateTime dateOfBirth,
        String identityNumber,
        LocalDateTime joinDate,
        String workStatus,
        String note) {}
