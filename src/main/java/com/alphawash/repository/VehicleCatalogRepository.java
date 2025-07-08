package com.alphawash.repository;

import com.alphawash.entity.VehicleCatalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleCatalogRepository extends JpaRepository<VehicleCatalog, Integer> {

    List<VehicleCatalog> findByBrandContainingIgnoreCase(String brand);

    List<VehicleCatalog> findBySize(String size);
}
