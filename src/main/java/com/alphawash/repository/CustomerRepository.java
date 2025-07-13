package com.alphawash.repository;

import com.alphawash.entity.Customer;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    @EntityGraph(attributePaths = {"vehicles", "vehicles.brand", "vehicles.model"})
    Optional<Customer> findByPhone(String phone);
}
