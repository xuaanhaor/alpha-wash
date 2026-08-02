package com.alphawash.repository;

import com.alphawash.entity.Model;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ModelRepository extends JpaRepository<Model, Long> {
    Optional<Model> findByCode(String code);

    List<Model> findByBrandCode(String brandCode);

    @Query(value = """
    SELECT 'M' || LPAD((COALESCE(MAX(SUBSTRING(code,2)::INT),0)+1)::TEXT,4,'0')
    FROM model
    """, nativeQuery = true)
    String generateModelCode();
}
