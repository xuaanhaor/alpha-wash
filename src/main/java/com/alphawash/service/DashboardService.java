package com.alphawash.service;

import com.alphawash.dto.DashboardDto;
import java.time.LocalDate;

public interface DashboardService {
    DashboardDto getDashboard(LocalDate start, LocalDate end, LocalDate compareStart, LocalDate compareEnd);
}
