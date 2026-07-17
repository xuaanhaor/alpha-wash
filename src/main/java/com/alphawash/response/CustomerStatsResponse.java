package com.alphawash.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerStatsResponse {
    private int totalVisits;
    private BigDecimal totalSpending;
    private BigDecimal avgInvoice;
    private LocalDateTime firstVisitDate;
    private LocalDateTime lastVisitDate;
    private List<SpendingMonthResponse> spendingByMonth;
    private List<ServiceBreakdownResponse> serviceBreakdown;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SpendingMonthResponse {
        private String month;
        private BigDecimal total;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ServiceBreakdownResponse {
        private String serviceName;
        private int count;
        private BigDecimal total;
    }
}
