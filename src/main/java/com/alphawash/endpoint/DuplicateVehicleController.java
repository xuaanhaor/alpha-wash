package com.alphawash.endpoint;

import com.alphawash.constant.Constant;
import com.alphawash.request.MergeVehiclesRequest;
import com.alphawash.response.DuplicateVehicleGroupResponse;
import com.alphawash.response.MergeVehicleLogResponse;
import com.alphawash.response.VehicleMergePreviewResponse;
import com.alphawash.response.VehicleMergeResponse;
import com.alphawash.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constant.API_DUPLICATE_VEHICLES)
@RequiredArgsConstructor
@Tag(name = "Duplicate Vehicles", description = "Admin duplicate vehicle detection and merge operations")
public class DuplicateVehicleController {

    private final VehicleService vehicleService;

    @Operation(summary = "List all duplicate license plate groups")
    @GetMapping
    public ResponseEntity<List<DuplicateVehicleGroupResponse>> listDuplicates() {
        return ResponseEntity.ok(vehicleService.findDuplicateVehicles());
    }

    @Operation(summary = "Merge duplicate vehicles into a primary vehicle")
    @PostMapping(Constant.MERGE_ENDPOINT)
    public ResponseEntity<VehicleMergeResponse> merge(@RequestBody MergeVehiclesRequest request) {
        return ResponseEntity.ok(vehicleService.mergeVehicles(request));
    }

    @Operation(summary = "Preview per-vehicle order/spending stats before merging")
    @GetMapping(Constant.PREVIEW_MERGE_ENDPOINT)
    public ResponseEntity<VehicleMergePreviewResponse> previewMerge(@RequestParam List<UUID> vehicleIds) {
        return ResponseEntity.ok(vehicleService.previewMerge(vehicleIds));
    }

    @Operation(summary = "List merge history logs")
    @GetMapping(Constant.LOGS_ENDPOINT)
    public ResponseEntity<List<MergeVehicleLogResponse>> getMergeLogs() {
        return ResponseEntity.ok(vehicleService.getMergeLogs());
    }
}
