package com.alphawash.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class CustomerListRepository {

    private static final Set<String> SORTABLE_FIELDS =
            Set.of("name", "totalSpending", "totalVisits", "lastVisitDate");

    private static final String FROM_JOIN =
            """
            FROM customer c
            LEFT JOIN (
                SELECT customer_id, COUNT(*) AS vehicle_count
                FROM vehicle
                WHERE delete_flag = false
                GROUP BY customer_id
            ) veh ON veh.customer_id = c.id
            LEFT JOIN (
                SELECT customer_id,
                       COUNT(DISTINCT id) AS total_visits,
                       COALESCE(SUM(total_price), 0) AS total_spending,
                       MAX(date) AS last_visit_date
                FROM orders
                WHERE delete_flag = false
                GROUP BY customer_id
            ) stats ON stats.customer_id = c.id
            """;

    @PersistenceContext
    private EntityManager em;

    public record Criteria(
            String search,
            String status,
            String segment,
            BigDecimal minSpending,
            BigDecimal maxSpending,
            Integer vehicleCount,
            LocalDate lastVisitFrom,
            LocalDate lastVisitTo,
            String sortBy,
            String sortDir) {}

    public record Row(
            UUID id,
            String customerName,
            String phone,
            String email,
            String avatarUrl,
            String status,
            LocalDateTime createdAt,
            int vehicleCount,
            int totalVisits,
            BigDecimal totalSpending,
            LocalDateTime lastVisitDate) {}

    public long count(Criteria criteria) {
        Map<String, Object> params = new HashMap<>();
        String where = buildWhere(criteria, params);
        Query query = em.createNativeQuery("SELECT COUNT(*) " + FROM_JOIN + where);
        params.forEach(query::setParameter);
        Number result = (Number) query.getSingleResult();
        return result.longValue();
    }

    public List<Row> search(Criteria criteria, int page, int size) {
        Map<String, Object> params = new HashMap<>();
        String where = buildWhere(criteria, params);
        String orderBy = buildOrderBy(criteria);

        String sql =
                """
                SELECT
                    c.id AS id,
                    c.customer_name AS customer_name,
                    c.phone AS phone,
                    c.email AS email,
                    c.avatar_url AS avatar_url,
                    COALESCE(c.status, 'ACTIVE') AS status,
                    c.created_at AS created_at,
                    COALESCE(veh.vehicle_count, 0) AS vehicle_count,
                    COALESCE(stats.total_visits, 0) AS total_visits,
                    COALESCE(stats.total_spending, 0) AS total_spending,
                    stats.last_visit_date AS last_visit_date
                """
                        + FROM_JOIN
                        + where
                        + orderBy
                        + " LIMIT :limit OFFSET :offset";

        @SuppressWarnings("unchecked")
        Query query = em.createNativeQuery(sql, Tuple.class);
        params.forEach(query::setParameter);
        query.setParameter("limit", size);
        query.setParameter("offset", (long) page * size);

        @SuppressWarnings("unchecked")
        List<Tuple> tuples = query.getResultList();

        return tuples.stream()
                .map(t -> new Row(
                        (UUID) t.get("id"),
                        (String) t.get("customer_name"),
                        (String) t.get("phone"),
                        (String) t.get("email"),
                        (String) t.get("avatar_url"),
                        (String) t.get("status"),
                        toLocalDateTime(t.get("created_at")),
                        toInt(t.get("vehicle_count")),
                        toInt(t.get("total_visits")),
                        toBigDecimal(t.get("total_spending")),
                        toLocalDateTime(t.get("last_visit_date"))))
                .toList();
    }

    private String buildWhere(Criteria c, Map<String, Object> params) {
        StringBuilder sb = new StringBuilder(" WHERE c.delete_flag = false ");

        if (c.search() != null && !c.search().isBlank()) {
            sb.append(
                    """
                    AND (c.customer_name ILIKE :search
                         OR c.phone ILIKE :search
                         OR EXISTS (
                             SELECT 1 FROM vehicle v2
                             WHERE v2.customer_id = c.id
                               AND v2.delete_flag = false
                               AND v2.license_plate ILIKE :search
                         ))
                    """);
            params.put("search", "%" + c.search().trim() + "%");
        }

        if (c.status() != null && !c.status().isBlank()) {
            sb.append(" AND COALESCE(c.status, 'ACTIVE') = :status ");
            params.put("status", c.status());
        }

        if (c.vehicleCount() != null) {
            if (c.vehicleCount() >= 2) {
                sb.append(" AND COALESCE(veh.vehicle_count, 0) >= :vehicleCount ");
            } else {
                sb.append(" AND COALESCE(veh.vehicle_count, 0) = :vehicleCount ");
            }
            params.put("vehicleCount", c.vehicleCount());
        }

        if (c.minSpending() != null) {
            sb.append(" AND COALESCE(stats.total_spending, 0) >= :minSpending ");
            params.put("minSpending", c.minSpending());
        }

        if (c.maxSpending() != null) {
            sb.append(" AND COALESCE(stats.total_spending, 0) <= :maxSpending ");
            params.put("maxSpending", c.maxSpending());
        }

        if (c.lastVisitFrom() != null) {
            sb.append(" AND stats.last_visit_date >= :lastVisitFrom ");
            params.put("lastVisitFrom", c.lastVisitFrom());
        }

        if (c.lastVisitTo() != null) {
            sb.append(" AND stats.last_visit_date < :lastVisitToExclusive ");
            params.put("lastVisitToExclusive", c.lastVisitTo().plusDays(1));
        }

        if (c.segment() != null && !c.segment().isBlank()) {
            switch (c.segment()) {
                case "NEW" -> sb.append(" AND c.created_at >= NOW() - INTERVAL '30 days' ");
                case "INACTIVE" -> sb.append(
                        " AND (stats.last_visit_date IS NULL OR stats.last_visit_date < NOW() - INTERVAL '60 days') ");
                default -> {
                    sb.append(
                            """
                            AND EXISTS (
                                SELECT 1 FROM customer_segment_membership m
                                WHERE m.customer_id = c.id AND m.segment_code = :segment
                            )
                            """);
                    params.put("segment", c.segment());
                }
            }
        }

        return sb.toString();
    }

    private String buildOrderBy(Criteria c) {
        String field = c.sortBy() != null && SORTABLE_FIELDS.contains(c.sortBy()) ? c.sortBy() : "lastVisitDate";
        String dir = "asc".equalsIgnoreCase(c.sortDir()) ? "ASC" : "DESC";

        String column =
                switch (field) {
                    case "name" -> "c.customer_name";
                    case "totalSpending" -> "COALESCE(stats.total_spending, 0)";
                    case "totalVisits" -> "COALESCE(stats.total_visits, 0)";
                    default -> "stats.last_visit_date";
                };

        String nulls = "lastVisitDate".equals(field) ? " NULLS LAST" : "";
        return " ORDER BY " + column + " " + dir + nulls;
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
