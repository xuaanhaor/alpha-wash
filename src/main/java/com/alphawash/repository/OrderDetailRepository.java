package com.alphawash.repository;

import com.alphawash.entity.OrderDetail;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    List<OrderDetail> findAllByOrderId(UUID orderId);

    Optional<OrderDetail> findByOrderId(UUID orderId);

    Optional<OrderDetail> findById(UUID id);

    @Query(value = "SELECT COUNT(*) FROM order_detail WHERE DATE(created_at) = :date", nativeQuery = true)
    long countByDay(@Param("date") LocalDate date);

    @Query(value = "SELECT * FROM order_detail WHERE code = :code AND delete_flag = false", nativeQuery = true)
    Optional<OrderDetail> findByCode(@Param("code") String code);

    List<OrderDetail> findByOrder_Code(String orderCode);

    List<OrderDetail> findByOrder_CodeIn(List<String> orderCodes);

    List<OrderDetail> findAllByVehicle_IdAndDeleteFlagFalse(UUID vehicleId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
            value = "UPDATE order_detail SET vehicle_id = :primaryVehicleId "
                    + "WHERE vehicle_id = :duplicateVehicleId AND delete_flag = false",
            nativeQuery = true)
    int reassignVehicle(
            @Param("duplicateVehicleId") UUID duplicateVehicleId, @Param("primaryVehicleId") UUID primaryVehicleId);
}
