package com.alphawash.request;

public record DashboardRequest(
    String startDate,
    String endDate,
    String compareStartDate,
    String compareEndDate
) {}
