package com.alphawash.repository;

import com.alphawash.entity.OrderService;
import com.alphawash.entity.Vehicle;
import com.alphawash.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderServiceRepository extends JpaRepository<OrderService, UUID> {

    List<OrderService> findByVehicle(Vehicle vehicle);

    List<OrderService> findByService(Service service);

    List<OrderService> findByStatus(String status);
}
