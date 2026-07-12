package com.alphawash.service.impl;

import com.alphawash.dto.DashboardDto;
import com.alphawash.dto.DashboardDto.*;
import com.alphawash.repository.DashboardRepository;
import com.alphawash.service.DashboardService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final DashboardRepository dashboardRepository;

    @Override
    public DashboardDto getDashboard(LocalDate start, LocalDate end, LocalDate compareStart, LocalDate compareEnd) {
        return DashboardDto.builder()
                .kpiSummary(buildKpiSummary(start, end, compareStart, compareEnd))
                .revenueDashboard(buildRevenueDashboard(start, end))
                .profitDashboard(buildProfitDashboard(start, end))
                .servicePerformance(buildServicePerformance(start, end))
                .customerAnalytics(buildCustomerAnalytics(start, end))
                .employeePerformance(dashboardRepository.getEmployeePerformance(start, end))
                .workshopOperations(dashboardRepository.getWorkshopOperations())
                .financialHealth(buildFinancialHealth(start, end))
                .businessGoals(buildBusinessGoals(start, end))
                .alerts(dashboardRepository.getAlerts())
                .build();
    }

    private KpiSummary buildKpiSummary(LocalDate start, LocalDate end, LocalDate compareStart, LocalDate compareEnd) {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        BigDecimal todayRevenue = dashboardRepository.getTotalRevenue(today, today);
        BigDecimal yesterdayRevenue = dashboardRepository.getTotalRevenue(yesterday, yesterday);
        BigDecimal periodRevenue = dashboardRepository.getTotalRevenue(start, end);
        BigDecimal comparePeriodRevenue = dashboardRepository.getTotalRevenue(compareStart, compareEnd);

        BigDecimal periodCost = dashboardRepository.getProductCost(start, end);
        BigDecimal comparePeriodCost = dashboardRepository.getProductCost(compareStart, compareEnd);
        BigDecimal periodProfit = periodRevenue.subtract(periodCost);
        BigDecimal comparePeriodProfit = comparePeriodRevenue.subtract(comparePeriodCost);

        int periodInvoices = dashboardRepository.getInvoiceCount(start, end);
        int comparePeriodInvoices = dashboardRepository.getInvoiceCount(compareStart, compareEnd);
        int periodVehicles = dashboardRepository.getVehicleCount(start, end);
        int comparePeriodVehicles = dashboardRepository.getVehicleCount(compareStart, compareEnd);

        BigDecimal avgInvoice = periodInvoices > 0
                ? periodRevenue.divide(BigDecimal.valueOf(periodInvoices), 0, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        BigDecimal compareAvgInvoice = comparePeriodInvoices > 0
                ? comparePeriodRevenue.divide(BigDecimal.valueOf(comparePeriodInvoices), 0, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        BigDecimal profitMargin = periodRevenue.compareTo(BigDecimal.ZERO) > 0
                ? periodProfit.multiply(BigDecimal.valueOf(100)).divide(periodRevenue, 1, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        BigDecimal compareProfitMargin = comparePeriodRevenue.compareTo(BigDecimal.ZERO) > 0
                ? comparePeriodProfit.multiply(BigDecimal.valueOf(100)).divide(comparePeriodRevenue, 1, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return KpiSummary.builder()
                .todayRevenue(todayRevenue)
                .yesterdayRevenue(yesterdayRevenue)
                .periodRevenue(periodRevenue)
                .comparePeriodRevenue(comparePeriodRevenue)
                .periodProfit(periodProfit)
                .comparePeriodProfit(comparePeriodProfit)
                .periodCost(periodCost)
                .periodInvoices(periodInvoices)
                .comparePeriodInvoices(comparePeriodInvoices)
                .periodVehicles(periodVehicles)
                .comparePeriodVehicles(comparePeriodVehicles)
                .avgInvoiceValue(avgInvoice)
                .compareAvgInvoiceValue(compareAvgInvoice)
                .profitMargin(profitMargin)
                .compareProfitMargin(compareProfitMargin)
                .revenueSparkline(dashboardRepository.getRevenueSparkline(14))
                .build();
    }

    private RevenueDashboard buildRevenueDashboard(LocalDate start, LocalDate end) {
        return RevenueDashboard.builder()
                .revenueByDay(dashboardRepository.getRevenueByDay(start, end))
                .revenueByMonth(dashboardRepository.getRevenueByMonth(12))
                .revenueByCategory(dashboardRepository.getRevenueByCategory(start, end))
                .revenueByPaymentMethod(dashboardRepository.getRevenueByPaymentMethod(start, end))
                .revenueByEmployee(dashboardRepository.getRevenueByEmployee(start, end))
                .build();
    }

    private ProfitDashboard buildProfitDashboard(LocalDate start, LocalDate end) {
        BigDecimal totalRevenue = dashboardRepository.getTotalRevenue(start, end);
        BigDecimal serviceRevenue = dashboardRepository.getServiceRevenue(start, end);
        BigDecimal productRevenue = dashboardRepository.getProductRevenue(start, end);
        BigDecimal totalCost = dashboardRepository.getProductCost(start, end);
        BigDecimal grossProfit = totalRevenue.subtract(totalCost);
        BigDecimal profitMargin = totalRevenue.compareTo(BigDecimal.ZERO) > 0
                ? grossProfit.multiply(BigDecimal.valueOf(100)).divide(totalRevenue, 1, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        BigDecimal tipTotal = dashboardRepository.getTipTotal(start, end);

        return ProfitDashboard.builder()
                .totalRevenue(totalRevenue)
                .serviceRevenue(serviceRevenue)
                .productRevenue(productRevenue)
                .totalCost(totalCost)
                .grossProfit(grossProfit)
                .profitMargin(profitMargin)
                .tipTotal(tipTotal)
                .build();
    }

    private ServicePerformance buildServicePerformance(LocalDate start, LocalDate end) {
        var topByCount = dashboardRepository.getTopServicesByCount(start, end, 10);
        var topByRevenue = dashboardRepository.getTopServicesByRevenue(start, end, 10);
        var categoryDist = dashboardRepository.getRevenueByCategory(start, end);
        double attachRate = dashboardRepository.getProductAttachRate(start, end);

        BigDecimal totalCatRevenue = categoryDist.stream()
                .map(CategoryRevenue::getRevenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        var distribution = categoryDist.stream().map(c -> CategoryDistribution.builder()
                .category(c.getCategory())
                .count(c.getCount())
                .revenue(c.getRevenue())
                .percentage(totalCatRevenue.compareTo(BigDecimal.ZERO) > 0
                        ? c.getRevenue().multiply(BigDecimal.valueOf(100))
                            .divide(totalCatRevenue, 1, RoundingMode.HALF_UP).doubleValue()
                        : 0)
                .build()
        ).toList();

        return ServicePerformance.builder()
                .topByCount(topByCount)
                .topByRevenue(topByRevenue)
                .categoryDistribution(distribution)
                .productAttachRate(BigDecimal.valueOf(attachRate))
                .build();
    }

    private CustomerAnalytics buildCustomerAnalytics(LocalDate start, LocalDate end) {
        int newCustomers = dashboardRepository.getNewCustomerCount(start, end);
        int returning = dashboardRepository.getReturningCustomerCount(start, end);
        int totalActive = dashboardRepository.getTotalActiveCustomers(start, end);
        double retentionRate = totalActive > 0 ? (double) returning / totalActive * 100 : 0;

        return CustomerAnalytics.builder()
                .newCustomers(newCustomers)
                .returningCustomers(returning)
                .retentionRate(retentionRate)
                .avgSpending(dashboardRepository.getAvgCustomerSpending(start, end))
                .avgLifetimeValue(dashboardRepository.getAvgCustomerSpending(
                        LocalDate.of(2020, 1, 1), end))
                .topCustomers(dashboardRepository.getTopCustomers(start, end, 10))
                .avgVisitFrequency(dashboardRepository.getAvgVisitFrequency(start, end))
                .totalCustomers(totalActive)
                .build();
    }

    private FinancialHealth buildFinancialHealth(LocalDate start, LocalDate end) {
        return FinancialHealth.builder()
                .outstandingPayments(dashboardRepository.getOutstandingPayments())
                .outstandingCount(dashboardRepository.getOutstandingCount())
                .inventoryValue(dashboardRepository.getInventoryValue())
                .lowStockCount(dashboardRepository.getLowStockCount())
                .outOfStockCount(dashboardRepository.getOutOfStockCount())
                .dailyCashFlow(dashboardRepository.getDailyCashFlow(start, end))
                .build();
    }

    private BusinessGoals buildBusinessGoals(LocalDate start, LocalDate end) {
        LocalDate now = LocalDate.now();
        int daysInMonth = now.lengthOfMonth();
        int daysLeft = daysInMonth - now.getDayOfMonth();
        BigDecimal periodRevenue = dashboardRepository.getTotalRevenue(start, end);
        BigDecimal periodCost = dashboardRepository.getProductCost(start, end);
        int periodVehicles = dashboardRepository.getVehicleCount(start, end);
        int totalCustomers = dashboardRepository.getTotalActiveCustomers(start, end);

        BigDecimal revenueTarget = BigDecimal.valueOf(100_000_000);
        BigDecimal profitTarget = BigDecimal.valueOf(50_000_000);
        BigDecimal vehicleTarget = BigDecimal.valueOf(200);
        BigDecimal customerTarget = BigDecimal.valueOf(100);

        return BusinessGoals.builder()
                .revenueGoal(buildGoal(revenueTarget, periodRevenue, daysLeft))
                .profitGoal(buildGoal(profitTarget, periodRevenue.subtract(periodCost), daysLeft))
                .vehicleGoal(buildGoal(vehicleTarget, BigDecimal.valueOf(periodVehicles), daysLeft))
                .customerGoal(buildGoal(customerTarget, BigDecimal.valueOf(totalCustomers), daysLeft))
                .build();
    }

    private GoalProgress buildGoal(BigDecimal target, BigDecimal current, int daysLeft) {
        double pct = target.compareTo(BigDecimal.ZERO) > 0
                ? current.multiply(BigDecimal.valueOf(100)).divide(target, 1, RoundingMode.HALF_UP).doubleValue()
                : 0;
        BigDecimal remaining = target.subtract(current).max(BigDecimal.ZERO);

        return GoalProgress.builder()
                .target(target)
                .current(current)
                .percentage(Math.min(pct, 100))
                .remaining(remaining)
                .daysLeft(daysLeft)
                .build();
    }
}
