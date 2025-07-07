package com.alphawash.repository;

import com.alphawash.entity.Customer;
import com.alphawash.dao.CustomerDao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID>, CustomerDao {

    List<Customer> findByNameContainingIgnoreCase(String name);

    Customer findByPhone(String phone);
}
