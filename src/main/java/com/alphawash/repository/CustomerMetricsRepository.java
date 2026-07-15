package com.alphawash.repository;

import com.alphawash.dto.CustomerSegmentDto.CustomerMetrics;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import org.springframework.stereotype.Repository;

@Repository
public class CustomerMetricsRepository {

    @PersistenceContext
    private EntityManager em;

    public List<CustomerMetrics> computeAllCustomerMetrics() {
        String sql = """
            SELECT
                c.id as customer_id,
                c.customer_name,
                c.phone,
                COALESCE(stats.visit_count, 0) as visit_count,
                COALESCE(EXTRACT(DAY FROM NOW() - stats.last_visit_date), 9999) as days_since_last_visit,
                COALESCE(stats.total_spending, 0) as total_spending,
                COALESCE(stats.avg_invoice, 0) as avg_invoice,
                COALESCE(stats.total_invoices, 0) as total_invoices,
                COALESCE(veh.vehicle_count, 0) as vehicle_count,
                EXTRACT(DAY FROM NOW() - c.created_at) as days_since_registration,
                COALESCE(stats.last_visit_date::text, '') as last_service_date,
                COALESCE(stats.visits_last_30, 0) as visits_last_30,
                COALESCE(stats.visits_last_90, 0) as visits_last_90,
                COALESCE(stypes.service_types, '') as service_types_used,
                COALESCE(vbrands.brands, '') as vehicle_brands
            FROM customer c
            LEFT JOIN (
                SELECT
                    o.customer_id,
                    COUNT(DISTINCT o.id) as visit_count,
                    MAX(o.date) as last_visit_date,
                    COALESCE(SUM(o.total_price), 0) as total_spending,
                    COALESCE(AVG(o.total_price), 0) as avg_invoice,
                    COUNT(DISTINCT o.id) as total_invoices,
                    COUNT(DISTINCT CASE WHEN o.date >= NOW() - INTERVAL '30 days' THEN o.id END) as visits_last_30,
                    COUNT(DISTINCT CASE WHEN o.date >= NOW() - INTERVAL '90 days' THEN o.id END) as visits_last_90
                FROM orders o
                WHERE o.delete_flag = false
                  AND UPPER(COALESCE(o.payment_status, '')) = 'DONE'
                GROUP BY o.customer_id
            ) stats ON stats.customer_id = c.id
            LEFT JOIN (
                SELECT v.customer_id, COUNT(*) as vehicle_count
                FROM vehicle v WHERE v.delete_flag = false
                GROUP BY v.customer_id
            ) veh ON veh.customer_id = c.id
            LEFT JOIN (
                SELECT o2.customer_id,
                       STRING_AGG(DISTINCT st.service_type_name, ',') as service_types
                FROM orders o2
                JOIN order_detail od ON od.order_code = o2.code
                JOIN order_service_dtl osd ON osd.order_detail_code = od.code
                JOIN service_catalog sc ON sc.code = osd.service_catalog_code
                JOIN service s ON s.code = sc.service_code
                LEFT JOIN service_type st ON st.code = s.service_type_code
                WHERE o2.delete_flag = false
                  AND UPPER(COALESCE(o2.payment_status, '')) = 'DONE'
                GROUP BY o2.customer_id
            ) stypes ON stypes.customer_id = c.id
            LEFT JOIN (
                SELECT v2.customer_id,
                       STRING_AGG(DISTINCT b.brand_name, ',') as brands
                FROM vehicle v2
                JOIN brands b ON b.code = v2.brand_code
                WHERE v2.delete_flag = false
                GROUP BY v2.customer_id
            ) vbrands ON vbrands.customer_id = c.id
            WHERE c.delete_flag = false
            """;

        @SuppressWarnings("unchecked")
        List<Tuple> results = em.createNativeQuery(sql, Tuple.class).getResultList();

        return results.stream().map(t -> {
            int visits = toInt(t.get("visit_count"));
            long daysSinceLast = toLong(t.get("days_since_last_visit"));
            long daysSinceReg = toLong(t.get("days_since_registration"));

            double avgInterval = 0;
            if (visits > 1 && daysSinceReg > 0) {
                avgInterval = (double) daysSinceReg / visits;
            }

            String serviceTypes = safeString(t.get("service_types_used"));
            String brands = safeString(t.get("vehicle_brands"));

            return CustomerMetrics.builder()
                    .customerId((UUID) t.get("customer_id"))
                    .customerName(safeString(t.get("customer_name")))
                    .phone(safeString(t.get("phone")))
                    .visitCount(visits)
                    .daysSinceLastVisit(daysSinceLast)
                    .totalSpending(toBigDecimal(t.get("total_spending")))
                    .avgInvoice(toBigDecimal(t.get("avg_invoice")))
                    .totalInvoices(toInt(t.get("total_invoices")))
                    .vehicleCount(toInt(t.get("vehicle_count")))
                    .daysSinceRegistration(daysSinceReg)
                    .avgVisitIntervalDays(avgInterval)
                    .visitsLast30Days(toInt(t.get("visits_last_30")))
                    .visitsLast90Days(toInt(t.get("visits_last_90")))
                    .lastServiceDate(safeString(t.get("last_service_date")))
                    .serviceTypesUsed(serviceTypes.isEmpty() ? List.of() : Arrays.asList(serviceTypes.split(",")))
                    .vehicleBrands(brands.isEmpty() ? List.of() : Arrays.asList(brands.split(",")))
                    .build();
        }).toList();
    }

    private BigDecimal toBigDecimal(Object val) {
        if (val == null) return BigDecimal.ZERO;
        if (val instanceof BigDecimal bd) return bd;
        if (val instanceof Number n) return BigDecimal.valueOf(n.doubleValue());
        try { return new BigDecimal(val.toString()); } catch (Exception e) { return BigDecimal.ZERO; }
    }

    private int toInt(Object val) {
        if (val == null) return 0;
        if (val instanceof Number n) return n.intValue();
        try { return Integer.parseInt(val.toString()); } catch (Exception e) { return 0; }
    }

    private long toLong(Object val) {
        if (val == null) return 0L;
        if (val instanceof Number n) return n.longValue();
        try { return Long.parseLong(val.toString()); } catch (Exception e) { return 0L; }
    }

    private String safeString(Object val) {
        return val != null ? val.toString() : "";
    }
}
