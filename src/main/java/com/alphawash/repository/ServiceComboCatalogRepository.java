package com.alphawash.repository;

import com.alphawash.entity.ServiceComboCatalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ServiceComboCatalogRepository extends JpaRepository<ServiceComboCatalog, Integer> {
    Optional<ServiceComboCatalog> findByCode(String code);
}
