package com.alphawash.repository;

import com.alphawash.entity.OrderDetail;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, UUID> {
    List<OrderDetail> findAllByOrderId(UUID orderId);

    Optional<OrderDetail> findByOrderId(UUID orderId);

    Optional<OrderDetail> findById(UUID id);
}
