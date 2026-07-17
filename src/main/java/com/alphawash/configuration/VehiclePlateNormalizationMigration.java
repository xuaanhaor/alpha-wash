package com.alphawash.configuration;

import com.alphawash.entity.Vehicle;
import com.alphawash.repository.VehicleRepository;
import com.alphawash.util.LicensePlateUtil;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Backfills normalized_license_plate for existing vehicles, reports any duplicate plates found,
 * and only applies the unique constraint on the column once no duplicates remain.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class VehiclePlateNormalizationMigration implements CommandLineRunner {

    private static final String UNIQUE_CONSTRAINT_NAME = "uq_vehicle_normalized_license_plate";

    private final VehicleRepository vehicleRepository;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        backfillNormalizedPlates();

        List<String> duplicatePlates = vehicleRepository.findDuplicateNormalizedPlates();
        if (duplicatePlates.isEmpty()) {
            applyUniqueConstraintIfAbsent();
        } else {
            logDuplicates(duplicatePlates);
        }
    }

    private void backfillNormalizedPlates() {
        List<Vehicle> toUpdate = vehicleRepository.findAll().stream()
                .filter(vehicle -> vehicle.getLicensePlate() != null)
                .filter(vehicle ->
                        !LicensePlateUtil.normalize(vehicle.getLicensePlate()).equals(vehicle.getNormalizedLicensePlate()))
                .peek(vehicle -> vehicle.setNormalizedLicensePlate(LicensePlateUtil.normalize(vehicle.getLicensePlate())))
                .toList();

        if (!toUpdate.isEmpty()) {
            vehicleRepository.saveAll(toUpdate);
            log.info("Backfilled normalized_license_plate for {} vehicle(s)", toUpdate.size());
        }
    }

    private void logDuplicates(List<String> duplicatePlates) {
        for (String plate : duplicatePlates) {
            List<Vehicle> duplicates = vehicleRepository.findAllByNormalizedLicensePlateAndDeleteFlagFalse(plate);
            String vehicleIds =
                    duplicates.stream().map(vehicle -> vehicle.getId().toString()).collect(Collectors.joining(", "));
            log.warn("Duplicate vehicle license plate detected [{}]: vehicle ids = [{}]", plate, vehicleIds);
        }
        log.warn(
                "{} duplicate license plate group(s) found; skipping unique constraint on"
                        + " vehicle.normalized_license_plate until duplicates are resolved via"
                        + " /api/admin/duplicate-vehicles/merge",
                duplicatePlates.size());
    }

    private void applyUniqueConstraintIfAbsent() {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM pg_constraint WHERE conname = ?", Integer.class, UNIQUE_CONSTRAINT_NAME);
        if (count != null && count == 0) {
            jdbcTemplate.execute(
                    "ALTER TABLE vehicle ADD CONSTRAINT " + UNIQUE_CONSTRAINT_NAME + " UNIQUE (normalized_license_plate)");
            log.info("Applied unique constraint {} on vehicle.normalized_license_plate", UNIQUE_CONSTRAINT_NAME);
        }
    }
}
