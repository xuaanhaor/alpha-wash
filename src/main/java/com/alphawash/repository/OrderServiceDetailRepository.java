package com.alphawash.repository;

import com.alphawash.entity.OrderServiceDetail;
import com.alphawash.entity.Employee;
import com.alphawash.entity.OrderService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderServiceDetailRepository extends JpaRepository<OrderServiceDetail, UUID> {

    List<OrderServiceDetail> findByOrderService(OrderService orderService);

    List<OrderServiceDetail> findByEmployee(Employee employee);
}
