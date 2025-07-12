package com.alphawash.repository;

import com.alphawash.entity.Model;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;

public interface ModelRepository extends JpaRepository<Model, Long> {
}
