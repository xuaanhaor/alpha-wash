package com.alphawash.repository;

import com.alphawash.entity.MergeVehicleLog;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MergeVehicleLogRepository extends JpaRepository<MergeVehicleLog, Long> {
    List<MergeVehicleLog> findAllByOrderByMergeDateDesc();
}
