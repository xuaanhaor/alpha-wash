package com.alphawash.repository;

import com.alphawash.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Integer> {

    // Tìm dịch vụ theo tên chính xác
    Service findByName(String name);
}
