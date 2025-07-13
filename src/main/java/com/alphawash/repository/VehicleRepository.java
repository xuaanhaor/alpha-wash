package com.alphawash.repository;

import com.alphawash.entity.Vehicle;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {
    Optional<Vehicle> findById(UUID id);

    Optional<Vehicle> findByLicensePlate(String licensePlate);
}
