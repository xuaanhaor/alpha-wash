package com.alphawash.repository;

import com.alphawash.entity.ServiceCatalog;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface ServiceCatalogRepository extends JpaRepository<ServiceCatalog, Long> {

    @Transactional
    @Query(value = """
            INSERT INTO service_catalog (size, price, service_id)
            VALUES (?::size, ?, ?)
            RETURNING *
            """, nativeQuery = true)
    ServiceCatalog insertReturning(String size, BigDecimal price, Long serviceId);

    @Transactional
    @Query(value = """
            UPDATE service_catalog
            SET size = ?::size,
                price = ?,
                service_id = ?
            WHERE id = ?
            RETURNING *
            """, nativeQuery = true)
    ServiceCatalog updateReturning(String size, BigDecimal price, Long serviceId, Long id);

    List<ServiceCatalog> findByService_Id(Long serviceId);
}
