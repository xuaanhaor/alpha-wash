package com.alphawash.repository;

import com.alphawash.entity.Customer;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    List<Customer> findByNameContainingIgnoreCase(String name);

    Customer findByPhone(String phone);
}
