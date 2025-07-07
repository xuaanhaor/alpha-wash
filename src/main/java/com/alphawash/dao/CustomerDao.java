package com.alphawash.dao;

import com.alphawash.entity.Customer;
import java.util.UUID;

public interface CustomerDao {

    Customer createCustomer(Customer customer);

    Customer updateCustomer(UUID id, Customer updated);

    void deleteCustomer(UUID id);
}
