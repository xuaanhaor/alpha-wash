package com.alphawash.repository;

import com.alphawash.dto.DashboardDto.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class DashboardRepository {

    @PersistenceContext
    private EntityManager em;

    public List<DailyRevenue> getRevenueByDay(LocalDate start, LocalDate end) {
        String sql = """
            SELECT DATE(o.date) as d,
                   COALESCE(SUM(o.total_price), 0) as revenue,
                   COALESCE(SUM(
                       (SELECT COALESCE(SUM(opd.unit_price * opd.quantity), 0)
                        FROM order_detail od2
                        JOIN order_product_dtl opd ON opd.order_detail_code = od2.code
                        WHERE od2.order_code = o.code)
                   ), 0) as product_cost,
                   COUNT(DISTINCT o.id) as invoice_count
            FROM orders o
            WHERE o.delete_flag = false
              AND DATE(o.date) BETWEEN :p_start_date AND :p_end_date
              AND UPPER(COALESCE(o.payment_status, '')) = 'DONE'
            GROUP BY DATE(o.date)
            ORDER BY d
            """;

        @SuppressWarnings("unchecked")
        List<Tuple> results = em.createNativeQuery(sql, Tuple.class)
                .setParameter("p_start_date", start)
                .setParameter("p_end_date", end)
                .getResultList();

        return results.stream().map(t -> DailyRevenue.builder()
                .date(t.get("d").toString())
                .revenue(toBigDecimal(t.get("revenue")))
                .cost(toBigDecimal(t.get("product_cost")))
                .profit(toBigDecimal(t.get("revenue")).subtract(toBigDecimal(t.get("product_cost"))))
                .invoiceCount(toInt(t.get("invoice_count")))
                .build()
        ).toList();
    }

    public List<MonthlyRevenue> getRevenueByMonth(int months) {
        String sql = """
            SELECT TO_CHAR(DATE(o.date), 'YYYY-MM') as m,
                   COALESCE(SUM(o.total_price), 0) as revenue,
                   0 as cost
            FROM orders o
            WHERE o.delete_flag = false
              AND o.date >= NOW() - INTERVAL '%d months'
              AND UPPER(COALESCE(o.payment_status, '')) = 'DONE'
            GROUP BY TO_CHAR(DATE(o.date), 'YYYY-MM')
            ORDER BY m
            """.formatted(months);

        @SuppressWarnings("unchecked")
        List<Tuple> results = em.createNativeQuery(sql, Tuple.class).getResultList();

        return results.stream().map(t -> MonthlyRevenue.builder()
                .month(t.get("m").toString())
                .revenue(toBigDecimal(t.get("revenue")))
                .cost(BigDecimal.ZERO)
                .profit(toBigDecimal(t.get("revenue")))
                .build()
        ).toList();
    }

    public List<CategoryRevenue> getRevenueByCategory(LocalDate start, LocalDate end) {
        String sql = """
            SELECT COALESCE(st.service_type_name, 'Khác') as category,
                   COALESCE(SUM(sc.price * osd.quantity), 0) as revenue,
                   COUNT(*) as cnt
            FROM orders o
            JOIN order_detail od ON od.order_code = o.code
            JOIN order_service_dtl osd ON osd.order_detail_code = od.code
            JOIN service_catalog sc ON sc.code = osd.service_catalog_code
            JOIN service s ON s.code = sc.service_code
            LEFT JOIN service_type st ON st.code = s.service_type_code
            WHERE o.delete_flag = false
              AND DATE(o.date) BETWEEN :p_start_date AND :p_end_date
              AND UPPER(COALESCE(o.payment_status, '')) = 'DONE'
            GROUP BY st.service_type_name
            ORDER BY revenue DESC
            """;

        @SuppressWarnings("unchecked")
        List<Tuple> results = em.createNativeQuery(sql, Tuple.class)
                .setParameter("p_start_date", start)
                .setParameter("p_end_date", end)
                .getResultList();

        return results.stream().map(t -> CategoryRevenue.builder()
                .category(t.get("category").toString())
                .revenue(toBigDecimal(t.get("revenue")))
                .count(toInt(t.get("cnt")))
                .build()
        ).toList();
    }

    public List<PaymentMethodRevenue> getRevenueByPaymentMethod(LocalDate start, LocalDate end) {
        String sql = """
            SELECT COALESCE(o.payment_type, 'Chưa xác định') as method,
                   COALESCE(SUM(o.total_price), 0) as amount,
                   COUNT(*) as cnt
            FROM orders o
            WHERE o.delete_flag = false
              AND DATE(o.date) BETWEEN :p_start_date AND :p_end_date
              AND UPPER(COALESCE(o.payment_status, '')) = 'DONE'
            GROUP BY o.payment_type
            ORDER BY amount DESC
            """;

        @SuppressWarnings("unchecked")
        List<Tuple> results = em.createNativeQuery(sql, Tuple.class)
                .setParameter("p_start_date", start)
                .setParameter("p_end_date", end)
                .getResultList();

        return results.stream().map(t -> PaymentMethodRevenue.builder()
                .method(t.get("method").toString())
                .amount(toBigDecimal(t.get("amount")))
                .count(toInt(t.get("cnt")))
                .build()
        ).toList();
    }

    public List<EmployeeRevenue> getRevenueByEmployee(LocalDate start, LocalDate end) {
        String sql = """
            SELECT e.name as employee_name,
                   COALESCE(SUM(o.total_price / GREATEST(array_length(string_to_array(od.employee_id, ','), 1), 1)), 0) as revenue,
                   COUNT(DISTINCT o.id) as invoice_count
            FROM orders o
            JOIN order_detail od ON od.order_code = o.code
            CROSS JOIN LATERAL unnest(string_to_array(od.employee_id, ',')) AS eid(val)
            JOIN employee e ON e.id = CAST(TRIM(eid.val) AS BIGINT)
            WHERE o.delete_flag = false
              AND DATE(o.date) BETWEEN :p_start_date AND :p_end_date
              AND UPPER(COALESCE(o.payment_status, '')) = 'DONE'
              AND od.employee_id IS NOT NULL AND od.employee_id != ''
            GROUP BY e.id, e.name
            ORDER BY revenue DESC
            """;

        @SuppressWarnings("unchecked")
        List<Tuple> results = em.createNativeQuery(sql, Tuple.class)
                .setParameter("p_start_date", start)
                .setParameter("p_end_date", end)
                .getResultList();

        return results.stream().map(t -> EmployeeRevenue.builder()
                .employeeName(t.get("employee_name").toString())
                .revenue(toBigDecimal(t.get("revenue")))
                .invoiceCount(toInt(t.get("invoice_count")))
                .build()
        ).toList();
    }

    public BigDecimal getTotalRevenue(LocalDate start, LocalDate end) {
        String sql = """
            SELECT COALESCE(SUM(o.total_price), 0) as total
            FROM orders o
            WHERE o.delete_flag = false
              AND DATE(o.date) BETWEEN :p_start_date AND :p_end_date
              AND UPPER(COALESCE(o.payment_status, '')) = 'DONE'
            """;

        Tuple t = (Tuple) em.createNativeQuery(sql, Tuple.class)
                .setParameter("p_start_date", start)
                .setParameter("p_end_date", end)
                .getSingleResult();

        return toBigDecimal(t.get("total"));
    }

    public BigDecimal getServiceRevenue(LocalDate start, LocalDate end) {
        String sql = """
            SELECT COALESCE(SUM(
                CASE WHEN osd.adjusted_price_flag = true THEN osd.adjusted_price * osd.quantity
                     ELSE sc.price * osd.quantity END
            ), 0) as total
            FROM orders o
            JOIN order_detail od ON od.order_code = o.code
            JOIN order_service_dtl osd ON osd.order_detail_code = od.code
            JOIN service_catalog sc ON sc.code = osd.service_catalog_code
            WHERE o.delete_flag = false
              AND DATE(o.date) BETWEEN :p_start_date AND :p_end_date
              AND UPPER(COALESCE(o.payment_status, '')) = 'DONE'
            """;

        Tuple t = (Tuple) em.createNativeQuery(sql, Tuple.class)
                .setParameter("p_start_date", start)
                .setParameter("p_end_date", end)
                .getSingleResult();

        return toBigDecimal(t.get("total"));
    }

    public BigDecimal getProductRevenue(LocalDate start, LocalDate end) {
        String sql = """
            SELECT COALESCE(SUM(
                CASE WHEN opd.adjusted_price_flag = true THEN opd.adjusted_price * opd.quantity
                     ELSE opd.unit_price * opd.quantity END
            ), 0) as total
            FROM orders o
            JOIN order_detail od ON od.order_code = o.code
            JOIN order_product_dtl opd ON opd.order_detail_code = od.code
            WHERE o.delete_flag = false
              AND DATE(o.date) BETWEEN :p_start_date AND :p_end_date
              AND UPPER(COALESCE(o.payment_status, '')) = 'DONE'
            """;

        Tuple t = (Tuple) em.createNativeQuery(sql, Tuple.class)
                .setParameter("p_start_date", start)
                .setParameter("p_end_date", end)
                .getSingleResult();

        return toBigDecimal(t.get("total"));
    }

    public BigDecimal getProductCost(LocalDate start, LocalDate end) {
        String sql = """
            SELECT COALESCE(SUM(p.cost_price * opd.quantity), 0) as total
            FROM orders o
            JOIN order_detail od ON od.order_code = o.code
            JOIN order_product_dtl opd ON opd.order_detail_code = od.code
            JOIN product p ON p.code = opd.product_code
            WHERE o.delete_flag = false
              AND DATE(o.date) BETWEEN :p_start_date AND :p_end_date
              AND UPPER(COALESCE(o.payment_status, '')) = 'DONE'
            """;

        Tuple t = (Tuple) em.createNativeQuery(sql, Tuple.class)
                .setParameter("p_start_date", start)
                .setParameter("p_end_date", end)
                .getSingleResult();

        return toBigDecimal(t.get("total"));
    }

    public BigDecimal getTipTotal(LocalDate start, LocalDate end) {
        String sql = """
            SELECT COALESCE(SUM(o.tip), 0) as total
            FROM orders o
            WHERE o.delete_flag = false
              AND DATE(o.date) BETWEEN :p_start_date AND :p_end_date
              AND UPPER(COALESCE(o.payment_status, '')) = 'DONE'
            """;

        Tuple t = (Tuple) em.createNativeQuery(sql, Tuple.class)
                .setParameter("p_start_date", start)
                .setParameter("p_end_date", end)
                .getSingleResult();

        return toBigDecimal(t.get("total"));
    }

    public int getInvoiceCount(LocalDate start, LocalDate end) {
        String sql = """
            SELECT COUNT(DISTINCT o.id) as cnt
            FROM orders o
            WHERE o.delete_flag = false
              AND DATE(o.date) BETWEEN :p_start_date AND :p_end_date
              AND UPPER(COALESCE(o.payment_status, '')) = 'DONE'
            """;

        Tuple t = (Tuple) em.createNativeQuery(sql, Tuple.class)
                .setParameter("p_start_date", start)
                .setParameter("p_end_date", end)
                .getSingleResult();

        return toInt(t.get("cnt"));
    }

    public int getVehicleCount(LocalDate start, LocalDate end) {
        String sql = """
            SELECT COUNT(DISTINCT od.vehicle_id) as cnt
            FROM orders o
            JOIN order_detail od ON od.order_code = o.code
            WHERE o.delete_flag = false
              AND DATE(o.date) BETWEEN :p_start_date AND :p_end_date
              AND UPPER(COALESCE(o.payment_status, '')) = 'DONE'
              AND od.vehicle_id IS NOT NULL
            """;

        Tuple t = (Tuple) em.createNativeQuery(sql, Tuple.class)
                .setParameter("p_start_date", start)
                .setParameter("p_end_date", end)
                .getSingleResult();

        return toInt(t.get("cnt"));
    }

    public List<BigDecimal> getRevenueSparkline(int days) {
        String sql = """
            WITH dates AS (
                SELECT generate_series(
                    CURRENT_DATE - INTERVAL '%d days',
                    CURRENT_DATE,
                    '1 day'::interval
                )::date as d
            )
            SELECT dates.d,
                   COALESCE(SUM(o.total_price), 0) as revenue
            FROM dates
            LEFT JOIN orders o ON DATE(o.date) = dates.d
                AND o.delete_flag = false
                AND UPPER(COALESCE(o.payment_status, '')) = 'DONE'
            GROUP BY dates.d
            ORDER BY dates.d
            """.formatted(days - 1);

        @SuppressWarnings("unchecked")
        List<Tuple> results = em.createNativeQuery(sql, Tuple.class).getResultList();

        return results.stream().map(t -> toBigDecimal(t.get("revenue"))).toList();
    }

    public List<ServiceStat> getTopServicesByCount(LocalDate start, LocalDate end, int limit) {
        String sql = """
            SELECT s.service_name, s.code,
                   COUNT(*) as cnt,
                   COALESCE(SUM(
                       CASE WHEN osd.adjusted_price_flag = true THEN osd.adjusted_price * osd.quantity
                            ELSE sc.price * osd.quantity END
                   ), 0) as revenue
            FROM orders o
            JOIN order_detail od ON od.order_code = o.code
            JOIN order_service_dtl osd ON osd.order_detail_code = od.code
            JOIN service_catalog sc ON sc.code = osd.service_catalog_code
            JOIN service s ON s.code = sc.service_code
            WHERE o.delete_flag = false
              AND DATE(o.date) BETWEEN :p_start_date AND :p_end_date
              AND UPPER(COALESCE(o.payment_status, '')) = 'DONE'
            GROUP BY s.code, s.service_name
            ORDER BY cnt DESC
            LIMIT :limit
            """;

        @SuppressWarnings("unchecked")
        List<Tuple> results = em.createNativeQuery(sql, Tuple.class)
                .setParameter("p_start_date", start)
                .setParameter("p_end_date", end)
                .setParameter("limit", limit)
                .getResultList();

        return results.stream().map(t -> {
            int cnt = toInt(t.get("cnt"));
            BigDecimal rev = toBigDecimal(t.get("revenue"));
            return ServiceStat.builder()
                    .serviceName(t.get("service_name").toString())
                    .serviceCode(t.get("code").toString())
                    .count(cnt)
                    .revenue(rev)
                    .avgPrice(cnt > 0 ? rev.divide(BigDecimal.valueOf(cnt), 0, RoundingMode.HALF_UP) : BigDecimal.ZERO)
                    .build();
        }).toList();
    }

    public List<ServiceStat> getTopServicesByRevenue(LocalDate start, LocalDate end, int limit) {
        String sql = """
            SELECT s.service_name, s.code,
                   COUNT(*) as cnt,
                   COALESCE(SUM(
                       CASE WHEN osd.adjusted_price_flag = true THEN osd.adjusted_price * osd.quantity
                            ELSE sc.price * osd.quantity END
                   ), 0) as revenue
            FROM orders o
            JOIN order_detail od ON od.order_code = o.code
            JOIN order_service_dtl osd ON osd.order_detail_code = od.code
            JOIN service_catalog sc ON sc.code = osd.service_catalog_code
            JOIN service s ON s.code = sc.service_code
            WHERE o.delete_flag = false
              AND DATE(o.date) BETWEEN :p_start_date AND :p_end_date
              AND UPPER(COALESCE(o.payment_status, '')) = 'DONE'
            GROUP BY s.code, s.service_name
            ORDER BY revenue DESC
            LIMIT :limit
            """;

        @SuppressWarnings("unchecked")
        List<Tuple> results = em.createNativeQuery(sql, Tuple.class)
                .setParameter("p_start_date", start)
                .setParameter("p_end_date", end)
                .setParameter("limit", limit)
                .getResultList();

        return results.stream().map(t -> {
            int cnt = toInt(t.get("cnt"));
            BigDecimal rev = toBigDecimal(t.get("revenue"));
            return ServiceStat.builder()
                    .serviceName(t.get("service_name").toString())
                    .serviceCode(t.get("code").toString())
                    .count(cnt)
                    .revenue(rev)
                    .avgPrice(cnt > 0 ? rev.divide(BigDecimal.valueOf(cnt), 0, RoundingMode.HALF_UP) : BigDecimal.ZERO)
                    .build();
        }).toList();
    }

    public double getProductAttachRate(LocalDate start, LocalDate end) {
        String sql = """
            SELECT
                COUNT(DISTINCT o.id) as total_orders,
                COUNT(DISTINCT CASE WHEN opd.id IS NOT NULL THEN o.id END) as orders_with_products
            FROM orders o
            JOIN order_detail od ON od.order_code = o.code
            LEFT JOIN order_product_dtl opd ON opd.order_detail_code = od.code
            WHERE o.delete_flag = false
              AND DATE(o.date) BETWEEN :p_start_date AND :p_end_date
              AND UPPER(COALESCE(o.payment_status, '')) = 'DONE'
            """;

        Tuple t = (Tuple) em.createNativeQuery(sql, Tuple.class)
                .setParameter("p_start_date", start)
                .setParameter("p_end_date", end)
                .getSingleResult();

        int total = toInt(t.get("total_orders"));
        int withProducts = toInt(t.get("orders_with_products"));
        return total > 0 ? (double) withProducts / total * 100 : 0;
    }

    public int getNewCustomerCount(LocalDate start, LocalDate end) {
        String sql = """
            SELECT COUNT(DISTINCT c.id) as cnt
            FROM customer c
            WHERE c.delete_flag = false
              AND DATE(c.created_at) BETWEEN :p_start_date AND :p_end_date
            """;

        Tuple t = (Tuple) em.createNativeQuery(sql, Tuple.class)
                .setParameter("p_start_date", start)
                .setParameter("p_end_date", end)
                .getSingleResult();

        return toInt(t.get("cnt"));
    }

    public int getReturningCustomerCount(LocalDate start, LocalDate end) {
        String sql = """
            SELECT COUNT(DISTINCT o.customer_id) as cnt
            FROM orders o
            WHERE o.delete_flag = false
              AND DATE(o.date) BETWEEN :p_start_date AND :p_end_date
              AND UPPER(COALESCE(o.payment_status, '')) = 'DONE'
              AND o.customer_id IN (
                  SELECT o2.customer_id FROM orders o2
                  WHERE o2.delete_flag = false
                    AND DATE(o2.date) < :p_start_date
                    AND UPPER(COALESCE(o2.payment_status, '')) = 'DONE'
              )
            """;

        Tuple t = (Tuple) em.createNativeQuery(sql, Tuple.class)
                .setParameter("p_start_date", start)
                .setParameter("p_end_date", end)
                .getSingleResult();

        return toInt(t.get("cnt"));
    }

    public int getTotalActiveCustomers(LocalDate start, LocalDate end) {
        String sql = """
            SELECT COUNT(DISTINCT o.customer_id) as cnt
            FROM orders o
            WHERE o.delete_flag = false
              AND DATE(o.date) BETWEEN :p_start_date AND :p_end_date
              AND UPPER(COALESCE(o.payment_status, '')) = 'DONE'
              AND o.customer_id IS NOT NULL
            """;

        Tuple t = (Tuple) em.createNativeQuery(sql, Tuple.class)
                .setParameter("p_start_date", start)
                .setParameter("p_end_date", end)
                .getSingleResult();

        return toInt(t.get("cnt"));
    }

    public BigDecimal getAvgCustomerSpending(LocalDate start, LocalDate end) {
        String sql = """
            SELECT COALESCE(AVG(customer_total), 0) as avg_spend
            FROM (
                SELECT o.customer_id, SUM(o.total_price) as customer_total
                FROM orders o
                WHERE o.delete_flag = false
                  AND DATE(o.date) BETWEEN :p_start_date AND :p_end_date
                  AND UPPER(COALESCE(o.payment_status, '')) = 'DONE'
                  AND o.customer_id IS NOT NULL
                GROUP BY o.customer_id
            ) sub
            """;

        Tuple t = (Tuple) em.createNativeQuery(sql, Tuple.class)
                .setParameter("p_start_date", start)
                .setParameter("p_end_date", end)
                .getSingleResult();

        return toBigDecimal(t.get("avg_spend"));
    }

    public List<VipCustomer> getTopCustomers(LocalDate start, LocalDate end, int limit) {
        String sql = """
            SELECT c.customer_name, c.phone,
                   SUM(o.total_price) as total_spent,
                   COUNT(DISTINCT o.id) as visit_count,
                   MAX(DATE(o.date))::text as last_visit
            FROM orders o
            JOIN customer c ON c.id = o.customer_id
            WHERE o.delete_flag = false
              AND DATE(o.date) BETWEEN :p_start_date AND :p_end_date
              AND UPPER(COALESCE(o.payment_status, '')) = 'DONE'
            GROUP BY c.id, c.customer_name, c.phone
            ORDER BY total_spent DESC
            LIMIT :limit
            """;

        @SuppressWarnings("unchecked")
        List<Tuple> results = em.createNativeQuery(sql, Tuple.class)
                .setParameter("p_start_date", start)
                .setParameter("p_end_date", end)
                .setParameter("limit", limit)
                .getResultList();

        return results.stream().map(t -> VipCustomer.builder()
                .customerName(safeString(t.get("customer_name")))
                .phone(safeString(t.get("phone")))
                .totalSpent(toBigDecimal(t.get("total_spent")))
                .visitCount(toInt(t.get("visit_count")))
                .lastVisit(safeString(t.get("last_visit")))
                .build()
        ).toList();
    }

    public double getAvgVisitFrequency(LocalDate start, LocalDate end) {
        String sql = """
            SELECT COALESCE(AVG(visit_count), 0) as avg_freq
            FROM (
                SELECT o.customer_id, COUNT(DISTINCT o.id) as visit_count
                FROM orders o
                WHERE o.delete_flag = false
                  AND DATE(o.date) BETWEEN :p_start_date AND :p_end_date
                  AND UPPER(COALESCE(o.payment_status, '')) = 'DONE'
                  AND o.customer_id IS NOT NULL
                GROUP BY o.customer_id
            ) sub
            """;

        Tuple t = (Tuple) em.createNativeQuery(sql, Tuple.class)
                .setParameter("p_start_date", start)
                .setParameter("p_end_date", end)
                .getSingleResult();

        return toDouble(t.get("avg_freq"));
    }

    public List<EmployeePerformance> getEmployeePerformance(LocalDate start, LocalDate end) {
        String sql = """
            SELECT e.id as emp_id, e.name as employee_name,
                   COALESCE(SUM(o.total_price / GREATEST(array_length(string_to_array(od.employee_id, ','), 1), 1)), 0) as revenue,
                   COUNT(DISTINCT o.id) as invoice_count,
                   COUNT(DISTINCT osd.code) as services_sold,
                   COALESCE((
                       SELECT COUNT(DISTINCT opd2.id)
                       FROM order_detail od2
                       JOIN order_product_dtl opd2 ON opd2.order_detail_code = od2.code
                       JOIN orders o2 ON o2.code = od2.order_code
                       WHERE od2.employee_id LIKE CONCAT('%%', CAST(e.id AS TEXT), '%%')
                         AND o2.delete_flag = false
                         AND DATE(o2.date) BETWEEN :p_start_date AND :p_end_date
                         AND UPPER(COALESCE(o2.payment_status, '')) = 'DONE'
                   ), 0) as products_sold
            FROM orders o
            JOIN order_detail od ON od.order_code = o.code
            LEFT JOIN order_service_dtl osd ON osd.order_detail_code = od.code
            CROSS JOIN LATERAL unnest(string_to_array(od.employee_id, ',')) AS eid(val)
            JOIN employee e ON e.id = CAST(TRIM(eid.val) AS BIGINT)
            WHERE o.delete_flag = false
              AND DATE(o.date) BETWEEN :p_start_date AND :p_end_date
              AND UPPER(COALESCE(o.payment_status, '')) = 'DONE'
              AND od.employee_id IS NOT NULL AND od.employee_id != ''
            GROUP BY e.id, e.name
            ORDER BY revenue DESC
            """;

        @SuppressWarnings("unchecked")
        List<Tuple> results = em.createNativeQuery(sql, Tuple.class)
                .setParameter("p_start_date", start)
                .setParameter("p_end_date", end)
                .getResultList();

        return results.stream().map(t -> {
            BigDecimal rev = toBigDecimal(t.get("revenue"));
            int invoices = toInt(t.get("invoice_count"));
            return EmployeePerformance.builder()
                    .employeeId(toLong(t.get("emp_id")))
                    .employeeName(t.get("employee_name").toString())
                    .revenue(rev)
                    .invoiceCount(invoices)
                    .avgInvoice(invoices > 0 ? rev.divide(BigDecimal.valueOf(invoices), 0, RoundingMode.HALF_UP) : BigDecimal.ZERO)
                    .servicesSold(toInt(t.get("services_sold")))
                    .productsSold(toInt(t.get("products_sold")))
                    .build();
        }).toList();
    }

    public WorkshopOperations getWorkshopOperations() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        String sql = """
            SELECT
                (SELECT COUNT(DISTINCT o1.id) FROM orders o1
                 WHERE o1.delete_flag = false AND DATE(o1.date) = :today
                   AND o1.checkin_time IS NOT NULL AND o1.checkout_time IS NULL
                   AND UPPER(COALESCE(o1.payment_status, '')) != 'DONE') as in_shop,
                (SELECT COUNT(DISTINCT o2.id) FROM orders o2
                 WHERE o2.delete_flag = false AND DATE(o2.date) = :today
                   AND UPPER(COALESCE(o2.payment_status, '')) = 'DONE') as completed,
                (SELECT COUNT(DISTINCT o3.id) FROM orders o3
                 WHERE o3.delete_flag = false AND DATE(o3.date) = :today) as today_orders,
                (SELECT COALESCE(AVG(EXTRACT(EPOCH FROM (
                    o4.date + o4.checkout_time - (o4.date + o4.checkin_time)
                )) / 60), 0)
                 FROM orders o4
                 WHERE o4.delete_flag = false AND DATE(o4.date) = :today
                   AND o4.checkin_time IS NOT NULL AND o4.checkout_time IS NOT NULL) as avg_turnaround
            """;

        Tuple t = (Tuple) em.createNativeQuery(sql, Tuple.class)
                .setParameter("today", today)
                .getSingleResult();

        return WorkshopOperations.builder()
                .vehiclesInShop(toInt(t.get("in_shop")))
                .completedToday(toInt(t.get("completed")))
                .todayOrders(toInt(t.get("today_orders")))
                .avgTurnaroundMinutes(toDouble(t.get("avg_turnaround")))
                .build();
    }

    public BigDecimal getOutstandingPayments() {
        String sql = """
            SELECT COALESCE(SUM(o.total_price), 0) as total
            FROM orders o
            WHERE o.delete_flag = false
              AND (UPPER(COALESCE(o.payment_status, '')) NOT IN ('DONE', 'CANCELLED'))
            """;

        Tuple t = (Tuple) em.createNativeQuery(sql, Tuple.class).getSingleResult();
        return toBigDecimal(t.get("total"));
    }

    public int getOutstandingCount() {
        String sql = """
            SELECT COUNT(*) as cnt
            FROM orders o
            WHERE o.delete_flag = false
              AND (UPPER(COALESCE(o.payment_status, '')) NOT IN ('DONE', 'CANCELLED'))
            """;

        Tuple t = (Tuple) em.createNativeQuery(sql, Tuple.class).getSingleResult();
        return toInt(t.get("cnt"));
    }

    public BigDecimal getInventoryValue() {
        String sql = """
            SELECT COALESCE(SUM(p.cost_price * p.current_stock), 0) as total
            FROM product p
            WHERE p.delete_flag = false AND p.is_active = true AND p.track_inventory = true
            """;

        Tuple t = (Tuple) em.createNativeQuery(sql, Tuple.class).getSingleResult();
        return toBigDecimal(t.get("total"));
    }

    public int getLowStockCount() {
        String sql = """
            SELECT COUNT(*) as cnt
            FROM product p
            WHERE p.delete_flag = false AND p.is_active = true AND p.track_inventory = true
              AND p.current_stock <= p.min_stock AND p.current_stock > 0
            """;

        Tuple t = (Tuple) em.createNativeQuery(sql, Tuple.class).getSingleResult();
        return toInt(t.get("cnt"));
    }

    public int getOutOfStockCount() {
        String sql = """
            SELECT COUNT(*) as cnt
            FROM product p
            WHERE p.delete_flag = false AND p.is_active = true AND p.track_inventory = true
              AND p.current_stock <= 0
            """;

        Tuple t = (Tuple) em.createNativeQuery(sql, Tuple.class).getSingleResult();
        return toInt(t.get("cnt"));
    }

    public List<DailyCashFlow> getDailyCashFlow(LocalDate start, LocalDate end) {
        String sql = """
            WITH dates AS (
                SELECT generate_series(CAST(:p_start_date AS date), CAST(:p_end_date AS date), CAST('1 day' AS interval))::date as d
            )
            SELECT dates.d::text as dt,
                   COALESCE(SUM(CASE WHEN UPPER(COALESCE(o.payment_status, '')) = 'DONE' THEN o.total_price ELSE 0 END), 0) as inflow,
                   0 as outflow
            FROM dates
            LEFT JOIN orders o ON DATE(o.date) = dates.d AND o.delete_flag = false
            GROUP BY dates.d
            ORDER BY dates.d
            """;

        @SuppressWarnings("unchecked")
        List<Tuple> results = em.createNativeQuery(sql, Tuple.class)
                .setParameter("p_start_date", start)
                .setParameter("p_end_date", end)
                .getResultList();

        return results.stream().map(t -> {
            BigDecimal inflow = toBigDecimal(t.get("inflow"));
            BigDecimal outflow = toBigDecimal(t.get("outflow"));
            return DailyCashFlow.builder()
                    .date(t.get("dt").toString())
                    .inflow(inflow)
                    .outflow(outflow)
                    .net(inflow.subtract(outflow))
                    .build();
        }).toList();
    }

    public List<Alert> getAlerts() {
        List<Alert> alerts = new ArrayList<>();

        // Low stock products
        String lowStockSql = """
            SELECT COUNT(*) as cnt FROM product p
            WHERE p.delete_flag = false AND p.is_active = true AND p.track_inventory = true
              AND p.current_stock <= p.min_stock AND p.current_stock > 0
            """;
        int lowStock = toInt(((Tuple) em.createNativeQuery(lowStockSql, Tuple.class).getSingleResult()).get("cnt"));
        if (lowStock > 0) {
            alerts.add(Alert.builder()
                    .type("LOW_STOCK").severity("warning")
                    .title("Sản phẩm sắp hết hàng")
                    .message(lowStock + " sản phẩm cần nhập thêm")
                    .count(lowStock).build());
        }

        // Out of stock
        String outOfStockSql = """
            SELECT COUNT(*) as cnt FROM product p
            WHERE p.delete_flag = false AND p.is_active = true AND p.track_inventory = true
              AND p.current_stock <= 0
            """;
        int outOfStock = toInt(((Tuple) em.createNativeQuery(outOfStockSql, Tuple.class).getSingleResult()).get("cnt"));
        if (outOfStock > 0) {
            alerts.add(Alert.builder()
                    .type("OUT_OF_STOCK").severity("critical")
                    .title("Sản phẩm hết hàng")
                    .message(outOfStock + " sản phẩm đã hết hàng")
                    .count(outOfStock).build());
        }

        // Outstanding payments
        String outstandingSql = """
            SELECT COUNT(*) as cnt, COALESCE(SUM(o.total_price), 0) as total
            FROM orders o
            WHERE o.delete_flag = false
              AND UPPER(COALESCE(o.payment_status, '')) NOT IN ('DONE', 'CANCELLED')
              AND o.date < NOW() - INTERVAL '1 day'
            """;
        Tuple outstanding = (Tuple) em.createNativeQuery(outstandingSql, Tuple.class).getSingleResult();
        int outstandingCnt = toInt(outstanding.get("cnt"));
        if (outstandingCnt > 0) {
            alerts.add(Alert.builder()
                    .type("OVERDUE_PAYMENT").severity("warning")
                    .title("Thanh toán quá hạn")
                    .message(outstandingCnt + " đơn hàng chưa thanh toán")
                    .count(outstandingCnt).build());
        }

        return alerts;
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

    private double toDouble(Object val) {
        if (val == null) return 0.0;
        if (val instanceof Number n) return n.doubleValue();
        try { return Double.parseDouble(val.toString()); } catch (Exception e) { return 0.0; }
    }

    private String safeString(Object val) {
        return val != null ? val.toString() : "";
    }
}
