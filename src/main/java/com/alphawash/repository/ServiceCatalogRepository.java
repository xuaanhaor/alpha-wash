package com.alphawash.repository;

import com.alphawash.entity.ServiceCatalog;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ServiceCatalogRepository extends JpaRepository<ServiceCatalog, Long> {

    @Transactional
    @Query(
            value =
                    """
            INSERT INTO service_catalog (size, price, service_id)
            VALUES (?::size, ?, ?)
            RETURNING *
            """,
            nativeQuery = true)
    ServiceCatalog insertReturning(String size, BigDecimal price, Long serviceId);

    @Transactional
    @Query(
            value =
                    """
            UPDATE service_catalog
            SET size = ?::size,
                price = ?,
                service_id = ?,
                temp_price = ?
            WHERE id = ?
            RETURNING *
            """,
            nativeQuery = true)
    ServiceCatalog updateReturning(String size, BigDecimal price, Long serviceId, Long id, BigDecimal tempPrice);

    List<ServiceCatalog> findByService_Id(Long serviceId);

    Optional<ServiceCatalog> findByCode(String code);

    @Query(
            value = "SELECT * FROM service_catalog sc " + "WHERE sc.service_code = :serviceCode "
                    + "AND sc.size = :size "
                    + "AND sc.delete_flag = false",
            nativeQuery = true)
    Optional<ServiceCatalog> findByServiceCodeAndSize(
            @Param("serviceCode") String serviceCode, @Param("size") String size);
}
