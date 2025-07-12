package com.alphawash.repository;

import com.alphawash.entity.OrderDetail;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, UUID> {}
