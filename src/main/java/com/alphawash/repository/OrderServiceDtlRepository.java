package com.alphawash.repository;

import com.alphawash.entity.OrderServiceDtl;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderServiceDtlRepository extends JpaRepository<OrderServiceDtl, Long> {
    @Query(value = "SELECT COUNT(*) FROM order_service_dtl WHERE DATE(created_at) = :date", nativeQuery = true)
    long countByDate(@Param("date") LocalDate date);

    @Query(value = "SELECT generate_osd_code()", nativeQuery = true)
    String generateOrderServiceDtlSequenceCode();

    @Modifying
    @Query(
            value =
                    "DELETE FROM order_service_dtl WHERE order_detail_code = :code AND service_catalog_code IN (:codes)",
            nativeQuery = true)
    void deleteByOrderDetailCodeAndServiceCatalogCodes(@Param("code") String code, @Param("codes") Set<String> codes);

    @Query(value = "SELECT * FROM order_service_dtl WHERE order_detail_code = :code", nativeQuery = true)
    List<OrderServiceDtl> findByOrderDetailCode(@Param("code") String code);
}
