package com.alphawash.repository;

import com.alphawash.entity.Order;
import com.alphawash.entity.Vehicle;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query(value = "SELECT * FROM get_vehicle_by_order_id(:p_order_id)", nativeQuery = true)
    Optional<Vehicle> findVehicleByOrderId(@Param("p_order_id") UUID orderId);

    Optional<Order> findById(UUID id);

    @Query(
            value =
                    "SELECT\n" +
                            "  -- ===== ORDER =====\n" +
                            "  o.id                AS order_id,\n" +
                            "  o.code              AS order_code,\n" +
                            "  o.date              AS order_date,\n" +
                            "  o.checkin_time      AS checkin_time,\n" +
                            "  o.checkout_time     AS checkout_time,\n" +
                            "  o.payment_status    AS payment_status,\n" +
                            "  o.payment_type      AS payment_type,\n" +
                            "  o.tip               AS tip,\n" +
                            "  o.vat               AS vat,\n" +
                            "  o.discount          AS discount,\n" +
                            "  o.total_price       AS total_price,\n" +
                            "  o.note              AS order_note,\n" +
                            "  o.delete_flag       AS delete_flag,\n" +
                            "  o.created_at        AS created_at,\n" +
                            "\n" +
                            "  -- ===== CUSTOMER =====\n" +
                            "  c.id                AS customer_id,\n" +
                            "  c.customer_name     AS customer_name,\n" +
                            "  c.phone             AS customer_phone,\n" +
                            "\n" +
                            "  -- ===== ORDER DETAIL =====\n" +
                            "  od.code             AS order_detail_code,\n" +
                            "  od.order_type       AS order_type,\n" +
                            "  od.status           AS order_detail_status,\n" +
                            "  od.note             AS order_detail_note,\n" +
                            "  od.employee_id      AS employee_id,\n" +
                            "\n" +
                            "  -- ===== VEHICLE =====\n" +
                            "  v.id                AS vehicle_id,\n" +
                            "  v.license_plate     AS license_plate,\n" +
                            "  v.image_url         AS vehicle_image_url,\n" +
                            "\n" +
                            "  -- ===== BRAND =====\n" +
                            "  b.id                AS brand_id,\n" +
                            "  b.code              AS brand_code,\n" +
                            "  b.brand_name        AS brand_name,\n" +
                            "\n" +
                            "  -- ===== MODEL =====\n" +
                            "  m.id                AS model_id,\n" +
                            "  m.code              AS model_code,\n" +
                            "  m.model_name        AS model_name,\n" +
                            "  m.size              AS model_size,\n" +
                            "\n" +
                            "  -- ===== SERVICE (SERVICE orders only) =====\n" +
                            "  s.id                AS service_id,\n" +
                            "  s.code              AS service_code,\n" +
                            "  s.service_name      AS service_name,\n" +
                            "  s.service_type_code AS service_type_code,\n" +
                            "\n" +
                            "  -- ===== ORDER_SERVICE_DTL (ADJUST) =====\n" +
                            "  osd.adjusted_price          AS adjusted_price,\n" +
                            "  osd.adjusted_price_flag     AS adjusted_price_flag,\n" +
                            "  osd.adjusted_price_reason   AS adjusted_price_reason,\n" +
                            "  osd.service_combo_catalog_code AS service_combo_catalog_code,\n" +
                            "\n" +
                            "  -- ===== SERVICE CATALOG (SERVICE orders only) =====\n" +
                            "  sc.id               AS service_catalog_id,\n" +
                            "  sc.code             AS service_catalog_code,\n" +
                            "  sc.price            AS service_catalog_price,\n" +
                            "  sc.size             AS service_catalog_size,\n" +
                            "\n" +
                            "  -- ===== COMBO CATALOG (COMBO orders) =====\n" +
                            "  scc.id                 AS combo_catalog_id,\n" +
                            "  scc.code               AS combo_catalog_code,\n" +
                            "  scb.combo_name         AS combo_name,\n" +
                            "  scc.combo_size         AS combo_size,\n" +
                            "  scc.price              AS combo_catalog_price,\n" +
                            "  scc.price_include_tax  AS combo_price_include_tax,\n" +
                            "  scq.service_catalog_code AS combo_service_catalog_code,\n" +
                            "  scq_s.service_name     AS combo_service_name,\n" +
                            "  scq.quality            AS combo_service_quantity,\n" +
                            "\n" +
                            "  -- ===== PROMOTION (optional) =====\n" +
                            "  p.id                   AS promo_id,\n" +
                            "  p.promo_code           AS promo_code,\n" +
                            "  p.promo_name           AS promo_name,\n" +
                            "  p.promo_type           AS promo_type,\n" +
                            "  p.value                AS promo_value,\n" +
                            "  p.start_date           AS promo_start_date,\n" +
                            "  p.end_date             AS promo_end_date,\n" +
                            "  ps.service_code        AS promo_service_code,\n" +
                            "  s2.service_name        AS promo_service_name,\n" +
                            "  ps.discount_amount     AS promo_discount_amount,\n" +
                            "  ps.discount_percent    AS promo_discount_percent\n" +
                            "\n" +
                            "FROM orders o\n" +
                            "LEFT JOIN customer c ON c.id = o.customer_id\n" +
                            "\n" +
                            "JOIN order_detail od ON od.order_code = o.code\n" +
                            "JOIN vehicle v ON v.id = od.vehicle_id\n" +
                            "JOIN brands b ON b.code = v.brand_code\n" +
                            "JOIN model  m ON m.code = v.model_code\n" +
                            "\n" +
                            "LEFT JOIN order_service_dtl osd ON osd.order_detail_code = od.code\n" +
                            "LEFT JOIN service_catalog sc ON sc.code = osd.service_catalog_code\n" +
                            "LEFT JOIN service s ON s.code = sc.service_code\n" +
                            "\n" +
                            "LEFT JOIN service_combo_catalog scc ON scc.code = osd.service_combo_catalog_code\n" +
                            "LEFT JOIN service_combo scb ON scb.code = scc.combo_code\n" +
                            "LEFT JOIN service_combo_quality scq ON scq.combo_catalog_code = scc.code\n" +
                            "LEFT JOIN service_catalog scq_sc ON scq_sc.code = scq.service_catalog_code\n" +
                            "LEFT JOIN service scq_s ON scq_s.code = scq_sc.service_code\n" +
                            "\n" +
                            "LEFT JOIN customer_promotion cp ON cp.order_code = o.code AND cp.delete_flag = false\n" +
                            "LEFT JOIN promotion p ON p.id = cp.promotion_id AND p.delete_flag = false\n" +
                            "LEFT JOIN promotion_service ps ON ps.promotion_id = p.id AND ps.delete_flag = false\n" +
                            "LEFT JOIN service s2 ON s2.code = ps.service_code\n" +
                            "\n" +
                            "ORDER BY o.created_at DESC;\n",
            nativeQuery = true
    )
    List<Object[]> getAllOrderRaw();

    @Query(
            value = "SELECT \n"
                    + "    o.id AS order_id,\n"
                    + "    o.code AS order_code,\n"
                    + "    o.date,\n"
                    + "    o.checkin_time,\n"
                    + "    o.checkout_time,\n"
                    + "    o.payment_status,\n"
                    + "    o.payment_type,\n"
                    + "    o.tip,\n"
                    + "    o.vat,\n"
                    + "    o.discount,\n"
                    + "    o.total_price,\n"
                    + "    o.note AS order_note,\n"
                    + "    o.delete_flag,\n"
                    + "\n"
                    + "    c.id AS customer_id,\n"
                    + "    c.customer_name,\n"
                    + "    c.phone,\n"
                    + "\n"
                    + "    od.code AS order_detail_code,\n"
                    + "    od.status,\n"
                    + "    od.note AS order_detail_note,\n"
                    + "    od.employee_id,\n"
                    + "\n"
                    + "    v.id AS vehicle_id,\n"
                    + "    v.license_plate,\n"
                    + "    v.image_url,\n"
                    + "\n"
                    + "    b.id AS brand_id,\n"
                    + "    b.brand_name,\n"
                    + "    b.code AS brand_code,\n"
                    + "\n"
                    + "    m.id AS model_id,\n"
                    + "    m.model_name,\n"
                    + "    m.code AS model_code,\n"
                    + "    m.size,\n"
                    + "\n"
                    + "    s.id AS service_id,\n"
                    + "    s.code AS service_code,\n"
                    + "    s.service_name,\n"
                    + "    s.service_type_code,\n"
                    + "\n"
                    + "    osd.adjusted_price,\n"
                    + "    osd.adjusted_price_flag,\n"
                    + "    osd.adjusted_price_reason,\n"
                    + "\n"
                    + "    sc.id AS service_catalog_id,\n"
                    + "    sc.code AS service_catalog_code,\n"
                    + "    sc.price,\n"
                    + "    sc.size AS service_catalog_size\n"
                    + "\n"
                    + "FROM orders o\n"
                    + "LEFT JOIN customer c ON c.id = o.customer_id\n"
                    + "JOIN order_detail od ON od.order_code = o.code\n"
                    + "JOIN vehicle v ON v.id = od.vehicle_id\n"
                    + "JOIN brands b ON b.code = v.brand_code\n"
                    + "JOIN model m ON m.code = v.model_code\n"
                    + "JOIN order_service_dtl osd ON osd.order_detail_code = od.code\n"
                    + "JOIN service_catalog sc ON sc.code = osd.service_catalog_code\n"
                    + "JOIN service s ON s.code = sc.service_code\n"
                    + "\n"
                    + "WHERE o.code = :code\n"
                    + "ORDER BY o.created_at DESC;",
            nativeQuery = true)
    List<Object[]> findFullByCode(@Param("code") String code);

    @Query(
            value =
                    "SELECT\n" +
                            "  -- ===== ORDER =====\n" +
                            "  o.id                AS order_id,\n" +
                            "  o.code              AS order_code,\n" +
                            "  o.date              AS order_date,\n" +
                            "  o.checkin_time      AS checkin_time,\n" +
                            "  o.checkout_time     AS checkout_time,\n" +
                            "  o.payment_status    AS payment_status,\n" +
                            "  o.payment_type      AS payment_type,\n" +
                            "  o.tip               AS tip,\n" +
                            "  o.vat               AS vat,\n" +
                            "  o.discount          AS discount,\n" +
                            "  o.total_price       AS total_price,\n" +
                            "  o.note              AS order_note,\n" +
                            "  o.delete_flag       AS delete_flag,\n" +
                            "  o.created_at        AS created_at,\n" +
                            "\n" +
                            "  -- ===== CUSTOMER =====\n" +
                            "  c.id                AS customer_id,\n" +
                            "  c.customer_name     AS customer_name,\n" +
                            "  c.phone             AS customer_phone,\n" +
                            "\n" +
                            "  -- ===== ORDER DETAIL =====\n" +
                            "  od.code             AS order_detail_code,\n" +
                            "  od.order_type       AS order_type,\n" +
                            "  od.status           AS order_detail_status,\n" +
                            "  od.note             AS order_detail_note,\n" +
                            "  od.employee_id      AS employee_id,\n" +
                            "\n" +
                            "  -- ===== VEHICLE =====\n" +
                            "  v.id                AS vehicle_id,\n" +
                            "  v.license_plate     AS license_plate,\n" +
                            "  v.image_url         AS vehicle_image_url,\n" +
                            "\n" +
                            "  -- ===== BRAND =====\n" +
                            "  b.id                AS brand_id,\n" +
                            "  b.code              AS brand_code,\n" +
                            "  b.brand_name        AS brand_name,\n" +
                            "\n" +
                            "  -- ===== MODEL =====\n" +
                            "  m.id                AS model_id,\n" +
                            "  m.code              AS model_code,\n" +
                            "  m.model_name        AS model_name,\n" +
                            "  m.size              AS model_size,\n" +
                            "\n" +
                            "  -- ===== SERVICE (SERVICE orders only) =====\n" +
                            "  s.id                AS service_id,\n" +
                            "  s.code              AS service_code,\n" +
                            "  s.service_name      AS service_name,\n" +
                            "  s.service_type_code AS service_type_code,\n" +
                            "\n" +
                            "  -- ===== ORDER_SERVICE_DTL (ADJUST) =====\n" +
                            "  osd.adjusted_price          AS adjusted_price,\n" +
                            "  osd.adjusted_price_flag     AS adjusted_price_flag,\n" +
                            "  osd.adjusted_price_reason   AS adjusted_price_reason,\n" +
                            "  osd.service_combo_catalog_code AS service_combo_catalog_code,\n" +
                            "\n" +
                            "  -- ===== SERVICE CATALOG (SERVICE orders only) =====\n" +
                            "  sc.id               AS service_catalog_id,\n" +
                            "  sc.code             AS service_catalog_code,\n" +
                            "  sc.price            AS service_catalog_price,\n" +
                            "  sc.size             AS service_catalog_size,\n" +
                            "\n" +
                            "  -- ===== COMBO CATALOG (COMBO orders) =====\n" +
                            "  scc.id                 AS combo_catalog_id,\n" +
                            "  scc.code               AS combo_catalog_code,\n" +
                            "  scb.combo_name         AS combo_name,\n" +
                            "  scc.combo_size         AS combo_size,\n" +
                            "  scc.price              AS combo_catalog_price,\n" +
                            "  scc.price_include_tax  AS combo_price_include_tax,\n" +
                            "  scq.service_catalog_code AS combo_service_catalog_code,\n" +
                            "  scq_s.service_name     AS combo_service_name,\n" + // ✅ NEW
                            "  scq.quality            AS combo_service_quantity,\n" +
                            "\n" +
                            "  -- ===== PROMOTION (optional) =====\n" +
                            "  p.id                   AS promo_id,\n" +
                            "  p.promo_code           AS promo_code,\n" +
                            "  p.promo_name           AS promo_name,\n" +
                            "  p.promo_type           AS promo_type,\n" +
                            "  p.value                AS promo_value,\n" +
                            "  p.start_date           AS promo_start_date,\n" +
                            "  p.end_date             AS promo_end_date,\n" +
                            "  ps.service_code        AS promo_service_code,\n" +
                            "  s2.service_name        AS promo_service_name,\n" +
                            "  ps.discount_amount     AS promo_discount_amount,\n" +
                            "  ps.discount_percent    AS promo_discount_percent\n" +
                            "\n" +
                            "FROM orders o\n" +
                            "LEFT JOIN customer c ON c.id = o.customer_id\n" +
                            "\n" +
                            "JOIN order_detail od ON od.order_code = o.code\n" +
                            "JOIN vehicle v ON v.id = od.vehicle_id\n" +
                            "JOIN brands b ON b.code = v.brand_code\n" +
                            "JOIN model  m ON m.code = v.model_code\n" +
                            "\n" +
                            "LEFT JOIN order_service_dtl osd ON osd.order_detail_code = od.code\n" +
                            "LEFT JOIN service_catalog sc ON sc.code = osd.service_catalog_code\n" +
                            "LEFT JOIN service s ON s.code = sc.service_code\n" +
                            "\n" +
                            "LEFT JOIN service_combo_catalog scc ON scc.code = osd.service_combo_catalog_code\n" +
                            "LEFT JOIN service_combo scb ON scb.code = scc.combo_code\n" +
                            "LEFT JOIN service_combo_quality scq ON scq.combo_catalog_code = scc.code\n" +
                            "LEFT JOIN service_catalog scq_sc ON scq_sc.code = scq.service_catalog_code\n" +
                            "LEFT JOIN service scq_s ON scq_s.code = scq_sc.service_code\n" +
                            "\n" +
                            "LEFT JOIN customer_promotion cp ON cp.order_code = o.code AND cp.delete_flag = false\n" +
                            "LEFT JOIN promotion p ON p.id = cp.promotion_id AND p.delete_flag = false\n" +
                            "LEFT JOIN promotion_service ps ON ps.promotion_id = p.id AND ps.delete_flag = false\n" +
                            "LEFT JOIN service s2 ON s2.code = ps.service_code\n" +
                            "\n" +
                            "WHERE o.id = :id\n" +
                            "ORDER BY o.created_at DESC;\n",
            nativeQuery = true
    )
    List<Object[]> findFullById(@Param("id") UUID id);

    @Query(value = "SELECT COUNT(*) FROM orders WHERE DATE(created_at) = :date", nativeQuery = true)
    long countByDate(@Param("date") LocalDate date);

    @Query(value = """
                SELECT EXISTS (
                    SELECT 1
                    FROM orders o
                    WHERE o.customer_id = :customerId
                      AND o.payment_status = 'DONE'
                      AND o.delete_flag = false
                      AND o.date < :cutoff
                )
            """, nativeQuery = true)
    boolean isOldCustomer(@Param("customerId") UUID customerId,
                          @Param("cutoff") LocalDateTime cutoff);

}
