package com.alphawash.repository;

import com.alphawash.entity.Vehicle;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {}
