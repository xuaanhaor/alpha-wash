package com.alphawash.dao.impl;

import com.alphawash.dao.CustomerDao;
import com.alphawash.entity.Customer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class CustomerDaoImpl implements CustomerDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Customer createCustomer(Customer customer) {
        entityManager.persist(customer);
        return customer;
    }

    @Override
    public Customer updateCustomer(UUID id, Customer updated) {
        Customer existing = entityManager.find(Customer.class, id);
        if (existing == null) return null;

        existing.setName(updated.getName());
        existing.setPhone(updated.getPhone());

        return entityManager.merge(existing);
    }

    @Override
    public void deleteCustomer(UUID id) {
        Customer customer = entityManager.find(Customer.class, id);
        if (customer != null) {
            entityManager.remove(customer);
        }
    }
}
