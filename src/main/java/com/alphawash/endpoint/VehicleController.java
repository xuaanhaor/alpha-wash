package com.alphawash.endpoint;

import com.alphawash.constant.Constant;
import com.alphawash.dto.BasicVehicleServiceUsedSearchDto;
import com.alphawash.dto.CarSizeDto;
import com.alphawash.dto.VehicleDto;
import com.alphawash.request.BasicCarSizeRequest;
import com.alphawash.request.VehicleRequest;
import com.alphawash.response.BasicCustomerVehicleDetailResponse;
import com.alphawash.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(Constant.API_VEHICLE)
@RequiredArgsConstructor
@Tag(name = "Vehicle", description = "Vehicle management operations")
public class VehicleController {

    private final VehicleService vehicleService;

    @GetMapping(Constant.SEARCH_ENDPOINT)
    public ResponseEntity<List<VehicleDto>> search() {
        var result = vehicleService.search();
        return ResponseEntity.ok(result);
    }

    @PostMapping(Constant.INSERT_ENDPOINT)
    public ResponseEntity<VehicleDto> insert(@RequestBody VehicleRequest request) {
        var result = vehicleService.insert(request);
        return ResponseEntity.ok(result);
    }

    @PatchMapping(Constant.UPDATE_WITH_PATH_PARAMETER)
    public ResponseEntity<Void> update(@RequestBody VehicleRequest request) {
        vehicleService.update(request);
        return ResponseEntity.noContent().build();
    }

    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Vehicle found"),
                @ApiResponse(responseCode = "404", description = "Vehicle not found"),
                @ApiResponse(responseCode = "400", description = "Invalid request data"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    @GetMapping(Constant.ID_PATH_PARAMETER)
    public ResponseEntity<VehicleDto> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(vehicleService.findById(id));
    }

    @Operation(summary = "Car size list retrieval")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Vehicle found"),
                @ApiResponse(responseCode = "404", description = "Vehicle not found"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    @GetMapping("/size")
    public ResponseEntity<List<CarSizeDto>> getCarSizes() {
        return ResponseEntity.ok(vehicleService.getCarSizes());
    }

    @Operation(summary = "Update car size")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Vehicle found"),
                @ApiResponse(responseCode = "404", description = "Vehicle not found"),
                @ApiResponse(responseCode = "400", description = "Invalid request data"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    @PostMapping("/size/update")
    public ResponseEntity<CarSizeDto> updateCarSize(@RequestBody BasicCarSizeRequest request) {
        return ResponseEntity.ok(vehicleService.updateCarSize(request));
    }

    @GetMapping("/services-used")
    public ResponseEntity<List<BasicVehicleServiceUsedSearchDto>> getVehicleServiceUsed() {
        return ResponseEntity.ok(vehicleService.searchVehicleServiceUsage());
    }

    @PostMapping("/services-used/detail")
    public ResponseEntity<BasicCustomerVehicleDetailResponse> getVehicleServiceUsedDetail(
            @RequestBody UUID customerId) {
        return ResponseEntity.ok(vehicleService.searchVehicleServiceUsageDetail(customerId));
    }
}
