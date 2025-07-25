package com.alphawash.repository;

import com.alphawash.entity.ServiceType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceTypeRepository extends JpaRepository<ServiceType, Long> {

    Optional<ServiceType> findByCode(String code);
}
