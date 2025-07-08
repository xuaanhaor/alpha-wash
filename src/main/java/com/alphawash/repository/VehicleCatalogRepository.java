package com.alphawash.repository;

import com.alphawash.entity.VehicleCatalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

@Repository
public interface VehicleCatalogRepository extends JpaRepository<VehicleCatalog, Integer> {

    @Query("SELECT DISTINCT vc.brand FROM VehicleCatalog vc")
    List<String> findAllDistinctBrands();

    List<VehicleCatalog> findByBrandContainingIgnoreCase(String brand);

    List<VehicleCatalog> findBySize(String size);
}
