package com.alphawash.repository;

import com.alphawash.entity.Product;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByCode(String code);

    Optional<Product> findByBarcode(String barcode);

    List<Product> findByDeleteFlagFalseOrderByProductNameAsc();

    List<Product> findByIsActiveTrueAndDeleteFlagFalseOrderByProductNameAsc();

    @Query("SELECT p FROM Product p WHERE p.category.code = :categoryCode AND p.deleteFlag = false")
    List<Product> findByCategoryCode(@Param("categoryCode") String categoryCode);

    @Query("SELECT p FROM Product p WHERE p.currentStock <= p.minStock AND p.trackInventory = true AND p.deleteFlag = false")
    List<Product> findLowStockProducts();

    @Query("SELECT p FROM Product p WHERE p.currentStock = 0 AND p.trackInventory = true AND p.deleteFlag = false")
    List<Product> findOutOfStockProducts();

    long countByDeleteFlagFalse();

    @Query("SELECT COUNT(p) FROM Product p WHERE p.currentStock <= p.minStock AND p.trackInventory = true AND p.deleteFlag = false")
    long countLowStock();

    @Query("SELECT COUNT(p) FROM Product p WHERE p.currentStock = 0 AND p.trackInventory = true AND p.deleteFlag = false")
    long countOutOfStock();

    @Query("SELECT COALESCE(SUM(p.currentStock * p.costPrice), 0) FROM Product p WHERE p.deleteFlag = false AND p.trackInventory = true")
    java.math.BigDecimal calculateTotalInventoryValue();

    long countByCodeStartingWith(String prefix);
}
