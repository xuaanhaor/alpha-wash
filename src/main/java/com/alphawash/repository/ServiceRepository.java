package com.alphawash.repository;

import com.alphawash.entity.Service;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRepository extends JpaRepository<Service, Long> {
    Optional<Service> findByCode(String code);
}
