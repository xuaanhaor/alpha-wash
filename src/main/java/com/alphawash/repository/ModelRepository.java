package com.alphawash.repository;

import com.alphawash.entity.Model;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModelRepository extends JpaRepository<Model, Long> {
    Optional<Model> findByCode(String code);

    List<Model> findByBrandCode(String brandCode);
}
