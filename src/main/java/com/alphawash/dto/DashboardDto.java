package com.alphawash.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDto {

    private KpiSummary kpiSummary;
    private RevenueDashboard revenueDashboard;
    private ProfitDashboard profitDashboard;
    private ServicePerformance servicePerformance;
    private CustomerAnalytics customerAnalytics;
    private List<EmployeePerformance> employeePerformance;
    private WorkshopOperations workshopOperations;
    private FinancialHealth financialHealth;
    private BusinessGoals businessGoals;
    private List<Alert> alerts;

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class KpiSummary {
        private BigDecimal todayRevenue;
        private BigDecimal yesterdayRevenue;
        private BigDecimal periodRevenue;
        private BigDecimal comparePeriodRevenue;
        private BigDecimal periodProfit;
        private BigDecimal comparePeriodProfit;
        private BigDecimal periodCost;
        private int periodInvoices;
        private int comparePeriodInvoices;
        private int periodVehicles;
        private int comparePeriodVehicles;
        private BigDecimal avgInvoiceValue;
        private BigDecimal compareAvgInvoiceValue;
        private BigDecimal profitMargin;
        private BigDecimal compareProfitMargin;
        private List<BigDecimal> revenueSparkline;
    }

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class RevenueDashboard {
        private List<DailyRevenue> revenueByDay;
        private List<MonthlyRevenue> revenueByMonth;
        private List<CategoryRevenue> revenueByCategory;
        private List<PaymentMethodRevenue> revenueByPaymentMethod;
        private List<EmployeeRevenue> revenueByEmployee;
    }

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class DailyRevenue {
        private String date;
        private BigDecimal revenue;
        private BigDecimal cost;
        private BigDecimal profit;
        private int invoiceCount;
    }

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class MonthlyRevenue {
        private String month;
        private BigDecimal revenue;
        private BigDecimal cost;
        private BigDecimal profit;
    }

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class CategoryRevenue {
        private String category;
        private BigDecimal revenue;
        private int count;
    }

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class PaymentMethodRevenue {
        private String method;
        private BigDecimal amount;
        private int count;
    }

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class EmployeeRevenue {
        private String employeeName;
        private BigDecimal revenue;
        private int invoiceCount;
    }

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class ProfitDashboard {
        private BigDecimal totalRevenue;
        private BigDecimal serviceRevenue;
        private BigDecimal productRevenue;
        private BigDecimal totalCost;
        private BigDecimal grossProfit;
        private BigDecimal profitMargin;
        private BigDecimal tipTotal;
    }

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class ServicePerformance {
        private List<ServiceStat> topByCount;
        private List<ServiceStat> topByRevenue;
        private List<CategoryDistribution> categoryDistribution;
        private BigDecimal productAttachRate;
    }

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class ServiceStat {
        private String serviceName;
        private String serviceCode;
        private int count;
        private BigDecimal revenue;
        private BigDecimal avgPrice;
    }

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class CategoryDistribution {
        private String category;
        private int count;
        private BigDecimal revenue;
        private double percentage;
    }

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class CustomerAnalytics {
        private int newCustomers;
        private int returningCustomers;
        private double retentionRate;
        private BigDecimal avgSpending;
        private BigDecimal avgLifetimeValue;
        private List<VipCustomer> topCustomers;
        private double avgVisitFrequency;
        private int totalCustomers;
    }

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class VipCustomer {
        private String customerName;
        private String phone;
        private BigDecimal totalSpent;
        private int visitCount;
        private String lastVisit;
    }

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class EmployeePerformance {
        private String employeeName;
        private Long employeeId;
        private BigDecimal revenue;
        private int invoiceCount;
        private BigDecimal avgInvoice;
        private int servicesSold;
        private int productsSold;
    }

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class WorkshopOperations {
        private int vehiclesInShop;
        private int completedToday;
        private int todayOrders;
        private double avgTurnaroundMinutes;
    }

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class FinancialHealth {
        private BigDecimal outstandingPayments;
        private int outstandingCount;
        private BigDecimal inventoryValue;
        private int lowStockCount;
        private int outOfStockCount;
        private List<DailyCashFlow> dailyCashFlow;
    }

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class DailyCashFlow {
        private String date;
        private BigDecimal inflow;
        private BigDecimal outflow;
        private BigDecimal net;
    }

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class BusinessGoals {
        private GoalProgress revenueGoal;
        private GoalProgress profitGoal;
        private GoalProgress vehicleGoal;
        private GoalProgress customerGoal;
    }

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class GoalProgress {
        private BigDecimal target;
        private BigDecimal current;
        private double percentage;
        private BigDecimal remaining;
        private int daysLeft;
    }

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Alert {
        private String type;
        private String severity;
        private String title;
        private String message;
        private int count;
    }
}
