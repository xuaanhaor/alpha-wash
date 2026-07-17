package com.alphawash.service.impl;

import com.alphawash.repository.AutoLinkRepository;
import com.alphawash.request.AutoLinkExecuteRequest;
import com.alphawash.response.AutoLinkConflictItemResponse;
import com.alphawash.response.AutoLinkCustomerOptionResponse;
import com.alphawash.response.AutoLinkExecuteResponse;
import com.alphawash.response.AutoLinkItemResponse;
import com.alphawash.response.AutoLinkPreviewResponse;
import com.alphawash.service.AutoLinkVehicleService;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AutoLinkVehicleServiceImpl implements AutoLinkVehicleService {

    private final AutoLinkRepository autoLinkRepository;

    @Override
    public AutoLinkPreviewResponse preview() {
        ClassifiedResult result = classify();

        long totalUnlinked = autoLinkRepository.countByCustomerIsNullAndDeleteFlagFalse();
        int noOrders = (int) totalUnlinked - result.previewItems().size() - result.conflictItems().size();

        return AutoLinkPreviewResponse.builder()
                .totalUnlinked((int) totalUnlinked)
                .safeToLink(result.previewItems().size())
                .conflicts(result.conflictItems().size())
                .noOrders(Math.max(noOrders, 0))
                .previewItems(result.previewItems())
                .conflictItems(result.conflictItems())
                .build();
    }

    @Override
    @Transactional
    public AutoLinkExecuteResponse execute(AutoLinkExecuteRequest request) {
        ClassifiedResult result = classify();

        long totalUnlinked = autoLinkRepository.countByCustomerIsNullAndDeleteFlagFalse();
        int noOrders = (int) totalUnlinked - result.previewItems().size() - result.conflictItems().size();
        int skipped = Math.max(noOrders, 0);
        int conflicts = result.conflictItems().size();

        if (request.isDryRun()) {
            return AutoLinkExecuteResponse.builder()
                    .linked(result.previewItems().size())
                    .skipped(skipped)
                    .conflicts(conflicts)
                    .build();
        }

        int linked = 0;
        for (AutoLinkItemResponse item : result.previewItems()) {
            int updated = autoLinkRepository.linkVehicleToCustomer(item.getVehicleId(), item.getSuggestedCustomerId());
            if (updated > 0) {
                linked++;
            }
        }

        return AutoLinkExecuteResponse.builder()
                .linked(linked)
                .skipped(skipped)
                .conflicts(conflicts)
                .build();
    }

    // ---------------------------------------------------------------------------
    // Internal helpers
    // ---------------------------------------------------------------------------

    /**
     * Runs the single native query and splits results into safe-to-link and conflict buckets.
     * Each row: [vehicle_id (UUID), license_plate (String), customer_id (UUID), customer_name (String), order_count (Long)]
     */
    private ClassifiedResult classify() {
        List<Object[]> rows = autoLinkRepository.findUnlinkedVehiclesWithCustomers();

        // Group rows by vehicle_id preserving insertion order (already sorted by the query)
        Map<UUID, List<Object[]>> byVehicle = new LinkedHashMap<>();
        for (Object[] row : rows) {
            UUID vehicleId = (UUID) row[0];
            byVehicle.computeIfAbsent(vehicleId, k -> new ArrayList<>()).add(row);
        }

        List<AutoLinkItemResponse> previewItems = new ArrayList<>();
        List<AutoLinkConflictItemResponse> conflictItems = new ArrayList<>();

        for (Map.Entry<UUID, List<Object[]>> entry : byVehicle.entrySet()) {
            UUID vehicleId = entry.getKey();
            List<Object[]> vehicleRows = entry.getValue();
            String licensePlate = (String) vehicleRows.get(0)[1];

            if (vehicleRows.size() == 1) {
                // Exactly one customer → safe to link
                UUID customerId = (UUID) vehicleRows.get(0)[2];
                String customerName = (String) vehicleRows.get(0)[3];
                int orderCount = ((Number) vehicleRows.get(0)[4]).intValue();

                previewItems.add(AutoLinkItemResponse.builder()
                        .vehicleId(vehicleId)
                        .licensePlate(licensePlate)
                        .suggestedCustomerId(customerId)
                        .suggestedCustomerName(customerName)
                        .orderCount(orderCount)
                        .build());
            } else {
                // Multiple customers → conflict, needs manual review
                List<AutoLinkCustomerOptionResponse> options = vehicleRows.stream()
                        .map(r -> AutoLinkCustomerOptionResponse.builder()
                                .customerId((UUID) r[2])
                                .customerName((String) r[3])
                                .orderCount(((Number) r[4]).intValue())
                                .build())
                        .toList();

                conflictItems.add(AutoLinkConflictItemResponse.builder()
                        .vehicleId(vehicleId)
                        .licensePlate(licensePlate)
                        .customerOptions(options)
                        .build());
            }
        }

        return new ClassifiedResult(previewItems, conflictItems);
    }

    private record ClassifiedResult(
            List<AutoLinkItemResponse> previewItems, List<AutoLinkConflictItemResponse> conflictItems) {}
}
