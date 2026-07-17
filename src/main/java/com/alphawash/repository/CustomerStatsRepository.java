package com.alphawash.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class CustomerStatsRepository {

    @PersistenceContext
    private EntityManager em;

    public record AggregateStats(
            int totalVisits,
            BigDecimal totalSpending,
            BigDecimal avgInvoice,
            LocalDateTime firstVisitDate,
            LocalDateTime lastVisitDate) {}

    public record MonthlySpending(String month, BigDecimal total) {}

    public record ServiceBreakdown(String serviceName, int count, BigDecimal total) {}

    public AggregateStats computeAggregateStats(UUID customerId) {
        String sql =
                """
                SELECT
                    COUNT(DISTINCT o.id) AS total_visits,
                    COALESCE(SUM(o.total_price), 0) AS total_spending,
                    COALESCE(AVG(o.total_price), 0) AS avg_invoice,
                    MIN(o.date) AS first_visit_date,
                    MAX(o.date) AS last_visit_date
                FROM orders o
                WHERE o.customer_id = :customerId AND o.delete_flag = false
                """;

        Tuple t = (Tuple) em.createNativeQuery(sql, Tuple.class)
                .setParameter("customerId", customerId)
                .getSingleResult();

        return new AggregateStats(
                toInt(t.get("total_visits")),
                toBigDecimal(t.get("total_spending")),
                toBigDecimal(t.get("avg_invoice")),
                toLocalDateTime(t.get("first_visit_date")),
                toLocalDateTime(t.get("last_visit_date")));
    }

    public List<MonthlySpending> computeSpendingByMonth(UUID customerId, int monthsBack) {
        String sql =
                """
                SELECT
                    to_char(date_trunc('month', o.date), 'YYYY-MM') AS month,
                    COALESCE(SUM(o.total_price), 0) AS total
                FROM orders o
                WHERE o.customer_id = :customerId
                  AND o.delete_flag = false
                  AND o.date >= date_trunc('month', NOW()) - (:monthsBack || ' months')::interval
                GROUP BY date_trunc('month', o.date)
                ORDER BY date_trunc('month', o.date)
                """;

        @SuppressWarnings("unchecked")
        List<Tuple> tuples = em.createNativeQuery(sql, Tuple.class)
                .setParameter("customerId", customerId)
                .setParameter("monthsBack", monthsBack)
                .getResultList();

        return tuples.stream()
                .map(t -> new MonthlySpending((String) t.get("month"), toBigDecimal(t.get("total"))))
                .toList();
    }

    public List<ServiceBreakdown> computeServiceBreakdown(UUID customerId) {
        String sql =
                """
                SELECT
                    s.service_name AS service_name,
                    COUNT(*) AS cnt,
                    COALESCE(SUM(
                        CASE WHEN osd.adjusted_price_flag THEN osd.adjusted_price ELSE sc.price END
                        * osd.quantity
                    ), 0) AS total
                FROM orders o
                JOIN order_detail od ON od.order_code = o.code
                JOIN order_service_dtl osd ON osd.order_detail_code = od.code
                JOIN service_catalog sc ON sc.code = osd.service_catalog_code
                JOIN service s ON s.code = sc.service_code
                WHERE o.customer_id = :customerId AND o.delete_flag = false
                GROUP BY s.service_name
                ORDER BY cnt DESC
                LIMIT 10
                """;

        @SuppressWarnings("unchecked")
        List<Tuple> tuples = em.createNativeQuery(sql, Tuple.class)
                .setParameter("customerId", customerId)
                .getResultList();

        return tuples.stream()
                .map(t -> new ServiceBreakdown(
                        (String) t.get("service_name"), toInt(t.get("cnt")), toBigDecimal(t.get("total"))))
                .toList();
    }

    private int toInt(Object val) {
        if (val == null) return 0;
        if (val instanceof Number n) return n.intValue();
        return Integer.parseInt(val.toString());
    }

    private BigDecimal toBigDecimal(Object val) {
        if (val == null) return BigDecimal.ZERO;
        if (val instanceof BigDecimal bd) return bd;
        if (val instanceof Number n) return BigDecimal.valueOf(n.doubleValue());
        return new BigDecimal(val.toString());
    }

    private LocalDateTime toLocalDateTime(Object val) {
        if (val == null) return null;
        if (val instanceof LocalDateTime ldt) return ldt;
        if (val instanceof java.sql.Timestamp ts) return ts.toLocalDateTime();
        return null;
    }
}
