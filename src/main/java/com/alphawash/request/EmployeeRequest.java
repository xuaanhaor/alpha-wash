package com.alphawash.request;

import java.time.LocalDate;

public record EmployeeRequest(
        String name,
        String phone,
        String bankName,
        String bankAccount,
        LocalDate dateOfBirth,
        String identityNumber,
        LocalDate joinDate,
        String workStatus,
        String note) {}
