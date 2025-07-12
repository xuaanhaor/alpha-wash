package com.alphawash.request;

import lombok.Builder;

@Builder
public record EmployeeRequest(String name, String phone, String note) {}
