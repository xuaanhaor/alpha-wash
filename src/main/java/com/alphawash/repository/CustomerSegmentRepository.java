package com.alphawash.repository;

import com.alphawash.entity.CustomerSegment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerSegmentRepository extends JpaRepository<CustomerSegment, Long> {

    List<CustomerSegment> findByDeleteFlagFalseAndIsActiveTrueOrderByDisplayOrderAsc();

    List<CustomerSegment> findByDeleteFlagFalseOrderByDisplayOrderAsc();

    Optional<CustomerSegment> findByCodeAndDeleteFlagFalse(String code);

    boolean existsByCodeAndDeleteFlagFalse(String code);
}
