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
            value = "SELECT \n"
                    + "    o.id AS order_id,                         \n"
                    + "    o.code AS order_code,                     \n"
                    + "    o.date,                                   \n"
                    + "    o.checkin_time,                           \n"
                    + "    o.checkout_time,                          \n"
                    + "    o.payment_status,                         \n"
                    + "    o.payment_type,                           \n"
                    + "    o.tip,                                    \n"
                    + "    o.vat,                                    \n"
                    + "    o.discount,                               \n"
                    + "    o.total_price,                            \n"
                    + "    o.note AS order_note,                     \n"
                    + "    o.delete_flag,                            \n"
                    + "\n"
                    + "    c.id AS customer_id,                      \n"
                    + "    c.customer_name,                          \n"
                    + "    c.phone,                                  \n"
                    + "\n"
                    + "    od.code AS order_detail_code,             \n"
                    + "    od.status,                                \n"
                    + "    od.note AS order_detail_note,             \n"
                    + "    od.employee_id,                           \n"
                    + "\n"
                    + "    v.id AS vehicle_id,                       \n"
                    + "    v.license_plate,                          \n"
                    + "    v.image_url,                              \n"
                    + "\n"
                    + "    b.id AS brand_id,                         \n"
                    + "    b.brand_name,                             \n"
                    + "    b.code AS brand_code,                     \n"
                    + "\n"
                    + "    m.id AS model_id,                         \n"
                    + "    m.model_name,                             \n"
                    + "    m.code AS model_code,                     \n"
                    + "    m.size,                                   \n"
                    + "\n"
                    + "    s.id AS service_id,                       \n"
                    + "    s.code AS service_code,                   \n"
                    + "    s.service_name,                           \n"
                    + "    s.service_type_code,                      \n"
                    + "\n"
                    + "    osd.adjusted_price,                       \n"
                    + "    osd.adjusted_price_flag,                  \n"
                    + "    osd.adjusted_price_reason,                \n"
                    + "\n"
                    + "    sc.id AS service_catalog_id,              \n"
                    + "    sc.code AS service_catalog_code,          \n"
                    + "    sc.price,                                 \n"
                    + "    sc.size AS service_catalog_size           \n"
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
                    + "ORDER BY o.created_at DESC;",
            nativeQuery = true)
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
                    + "    sc.size AS service_catalog_size,\n"
                    + "\n"
                    + "    p.id AS promo_id,\n"
                    + "    p.promo_code,\n"
                    + "    p.promo_name,\n"
                    + "    p.promo_type,\n"
                    + "    p.value AS promo_value,\n"
                    + "    p.start_date AS promo_start_date,\n"
                    + "    p.end_date AS promo_end_date,\n"
                    + "\n"
                    + "    ps.service_code AS promo_service_code,\n"
                    + "    s2.service_name AS promo_service_name,\n"
                    + "    ps.discount_amount AS promo_discount_amount,\n"
                    + "    ps.discount_percent AS promo_discount_percent\n"
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
                    + "LEFT JOIN customer_promotion cp \n"
                    + "       ON cp.order_code = o.code \n"
                    + "      AND cp.delete_flag = false\n"

                    + "LEFT JOIN promotion p \n"
                    + "       ON p.id = cp.promotion_id \n"
                    + "      AND p.delete_flag = false\n"

                    + "LEFT JOIN promotion_service ps \n"
                    + "       ON ps.promotion_id = p.id \n"
                    + "      AND ps.delete_flag = false\n"
                    + "LEFT JOIN service s2 ON s2.code = ps.service_code\n"
                    + "\n"
                    + "WHERE o.id = :id\n"
                    + "ORDER BY o.created_at DESC;",
            nativeQuery = true)
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
