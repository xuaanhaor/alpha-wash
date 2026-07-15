package com.alphawash.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerSegmentDto {

    private Long id;
    private String code;
    private String segmentName;
    private String description;
    private String color;
    private String icon;
    private String conditions;
    private String logicOperator;
    private Boolean isSystem;
    private Integer displayOrder;
    private Boolean isActive;
    private int customerCount;

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class CustomerMetrics {
        private UUID customerId;
        private String customerName;
        private String phone;
        private int visitCount;
        private long daysSinceLastVisit;
        private BigDecimal totalSpending;
        private BigDecimal avgInvoice;
        private int totalInvoices;
        private int vehicleCount;
        private long daysSinceRegistration;
        private double avgVisitIntervalDays;
        private int visitsLast30Days;
        private int visitsLast90Days;
        private String lastServiceDate;
        private List<String> serviceTypesUsed;
        private List<String> vehicleBrands;
    }

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class SegmentPreview {
        private String segmentName;
        private int matchCount;
        private List<PreviewCustomer> customers;
    }

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class PreviewCustomer {
        private UUID customerId;
        private String customerName;
        private String phone;
        private BigDecimal totalSpending;
        private int visitCount;
    }

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class SegmentDashboard {
        private List<SegmentSummary> segments;
        private List<PreviewCustomer> topSpenders;
        private List<PreviewCustomer> atRiskCustomers;
    }

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class SegmentSummary {
        private String code;
        private String segmentName;
        private String color;
        private String icon;
        private int customerCount;
        private int lastMonthCount;
        private int growth;
    }

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class CustomerWithSegments {
        private UUID customerId;
        private String customerName;
        private String phone;
        private List<SegmentBadge> segments;
    }

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class SegmentBadge {
        private String code;
        private String segmentName;
        private String color;
        private String icon;
    }
}
