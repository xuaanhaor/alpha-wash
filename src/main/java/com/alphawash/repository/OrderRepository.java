package com.alphawash.repository;

import com.alphawash.entity.Order;
import com.alphawash.entity.Vehicle;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findByCustomer_IdAndDeleteFlagFalseOrderByDateDesc(UUID customerId, Pageable pageable);

    /**
     * Migrate tất cả orders gắn với duplicate vehicle (qua order_detail) sang primary customer.
     * Dùng native SQL để tránh entity-state issues (detached entities) và xử lý đúng cả
     * orders có customer_id = NULL.
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
            value = "UPDATE orders SET customer_id = :primaryCustomerId "
                    + "WHERE code IN ("
                    + "  SELECT order_code FROM order_detail "
                    + "  WHERE vehicle_id = :duplicateVehicleId AND delete_flag = false"
                    + ") AND delete_flag = false",
            nativeQuery = true)
    int reassignCustomerByVehicle(
            @Param("duplicateVehicleId") UUID duplicateVehicleId,
            @Param("primaryCustomerId") UUID primaryCustomerId);

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
                    + "    osd.quantity,                              \n"
                    + "\n"
                    + "    sc.id AS service_catalog_id,              \n"
                    + "    sc.code AS service_catalog_code,          \n"
                    + "    sc.price,                                 \n"
                    + "    sc.size AS service_catalog_size,          \n"
                    + "    osd.service_catalog_code AS osd_catalog_code \n"
                    + "\n"
                    + "FROM orders o\n"
                    + "LEFT JOIN customer c ON c.id = o.customer_id\n"
                    + "JOIN order_detail od ON od.order_code = o.code\n"
                    + "JOIN vehicle v ON v.id = od.vehicle_id\n"
                    + "JOIN brands b ON b.code = v.brand_code\n"
                    + "JOIN model m ON m.code = v.model_code\n"
                    + "LEFT JOIN order_service_dtl osd ON osd.order_detail_code = od.code\n"
                    + "LEFT JOIN service_catalog sc ON sc.code = osd.service_catalog_code\n"
                    + "LEFT JOIN service s ON s.code = sc.service_code\n"
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
                    + "    osd.quantity,\n"
                    + "\n"
                    + "    sc.id AS service_catalog_id,\n"
                    + "    sc.code AS service_catalog_code,\n"
                    + "    sc.price,\n"
                    + "    sc.size AS service_catalog_size,\n"
                    + "    osd.service_catalog_code AS osd_catalog_code\n"
                    + "\n"
                    + "FROM orders o\n"
                    + "LEFT JOIN customer c ON c.id = o.customer_id\n"
                    + "JOIN order_detail od ON od.order_code = o.code\n"
                    + "JOIN vehicle v ON v.id = od.vehicle_id\n"
                    + "JOIN brands b ON b.code = v.brand_code\n"
                    + "JOIN model m ON m.code = v.model_code\n"
                    + "LEFT JOIN order_service_dtl osd ON osd.order_detail_code = od.code\n"
                    + "LEFT JOIN service_catalog sc ON sc.code = osd.service_catalog_code\n"
                    + "LEFT JOIN service s ON s.code = sc.service_code\n"
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
                    + "    osd.quantity,\n"
                    + "\n"
                    + "    sc.id AS service_catalog_id,\n"
                    + "    sc.code AS service_catalog_code,\n"
                    + "    sc.price,\n"
                    + "    sc.size AS service_catalog_size,\n"
                    + "    osd.service_catalog_code AS osd_catalog_code\n"
                    + "\n"
                    + "FROM orders o\n"
                    + "LEFT JOIN customer c ON c.id = o.customer_id\n"
                    + "JOIN order_detail od ON od.order_code = o.code\n"
                    + "JOIN vehicle v ON v.id = od.vehicle_id\n"
                    + "JOIN brands b ON b.code = v.brand_code\n"
                    + "JOIN model m ON m.code = v.model_code\n"
                    + "LEFT JOIN order_service_dtl osd ON osd.order_detail_code = od.code\n"
                    + "LEFT JOIN service_catalog sc ON sc.code = osd.service_catalog_code\n"
                    + "LEFT JOIN service s ON s.code = sc.service_code\n"
                    + "\n"
                    + "WHERE o.id = :id\n"
                    + "ORDER BY o.created_at DESC;",
            nativeQuery = true)
    List<Object[]> findFullById(@Param("id") UUID id);

    List<Order> findByIdIn(List<UUID> ids);

    @Query(value = "SELECT COUNT(*) FROM orders WHERE DATE(created_at) = :date", nativeQuery = true)
    long countByDate(@Param("date") LocalDate date);

    @Query(value = "SELECT COUNT(*) FROM orders", nativeQuery = true)
    long countAllOrdersForPaging();

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
                    + "    osd.quantity,                              \n"
                    + "\n"
                    + "    sc.id AS service_catalog_id,              \n"
                    + "    sc.code AS service_catalog_code,          \n"
                    + "    sc.price,                                 \n"
                    + "    sc.size AS service_catalog_size,          \n"
                    + "    osd.service_catalog_code AS osd_catalog_code \n"
                    + "\n"
                    + "FROM orders o\n"
                    + "LEFT JOIN customer c ON c.id = o.customer_id\n"
                    + "JOIN order_detail od ON od.order_code = o.code\n"
                    + "JOIN vehicle v ON v.id = od.vehicle_id\n"
                    + "JOIN brands b ON b.code = v.brand_code\n"
                    + "JOIN model m ON m.code = v.model_code\n"
                    + "LEFT JOIN order_service_dtl osd ON osd.order_detail_code = od.code\n"
                    + "LEFT JOIN service_catalog sc ON sc.code = osd.service_catalog_code\n"
                    + "LEFT JOIN service s ON s.code = sc.service_code\n"
                    + "\n"
                    + "WHERE o.code IN (\n"
                    + "    SELECT code FROM orders ORDER BY created_at DESC LIMIT :limit OFFSET :offset\n"
                    + ")\n"
                    + "ORDER BY o.created_at DESC",
            nativeQuery = true)
    List<Object[]> getAllOrderRawPaged(@Param("limit") int limit, @Param("offset") int offset);
}
