package com.alphawash.repository;

import com.alphawash.entity.Order;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    @Query(value = "SELECT * FROM get_full_orders()", nativeQuery = true)
    List<Object[]> getAllOrdersRaw();
}
