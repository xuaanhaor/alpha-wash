package com.alphawash.repository;

import com.alphawash.entity.Order;
import com.alphawash.entity.Vehicle;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    @Query(value = "SELECT * FROM get_full_orders()", nativeQuery = true)
    List<Object[]> getAllOrdersRaw();

    @Query(value = "SELECT * FROM get_vehicle_by_order_id(:p_order_id)", nativeQuery = true)
    Optional<Vehicle> findVehicleByOrderId(@Param("p_order_id") UUID orderId);

    Optional<Order> findById(UUID id);

    @Query(value = "SELECT * FROM get_full_order_by_id(?1)", nativeQuery = true)
    List<Object[]> getFullOrderById(UUID orderId);
}
