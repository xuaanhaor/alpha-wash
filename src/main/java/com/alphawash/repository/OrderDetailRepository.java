package com.alphawash.repository;

import com.alphawash.entity.OrderDetail;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
