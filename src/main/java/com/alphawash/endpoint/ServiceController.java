package com.alphawash.endpoint;

import com.alphawash.converter.ServiceConverter;
import com.alphawash.dto.ServiceDto;
import com.alphawash.request.ServiceRequest;
import com.alphawash.response.ServiceResponse;
import com.alphawash.service.ServiceService;
import com.alphawash.util.ObjectUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.alphawash.constant.Constant.*;

@RestController
@RequestMapping(API_SERVICE)
@RequiredArgsConstructor
@Tag(name = "Service", description = "API for managing vehicle services")
public class ServiceController {

    private final ServiceService serviceService;

    @Operation(summary = "Get all services")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of services"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(ROOT)
    public ResponseEntity<List<ServiceResponse>> getAll() {
        List<ServiceDto> dtos = serviceService.getAll();
        return ResponseEntity.ok(ServiceConverter.INSTANCE.toResponse(dtos));
    }

    @Operation(summary = "Get a service by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Service found"),
            @ApiResponse(responseCode = "404", description = "Service not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(ID_PATH_PARAMETER)
    public ResponseEntity<ServiceResponse> getById(@PathVariable Long id) {
        ServiceDto dto = serviceService.getById(id);
        if (ObjectUtils.isNull(dto)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ServiceConverter.INSTANCE.toResponse(dto));
    }

    @Operation(summary = "Create a new service")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Service created successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(INSERT_ENDPOINT)
    public ResponseEntity<ServiceResponse> create(@RequestBody ServiceRequest request) {
        ServiceDto dto = ServiceConverter.INSTANCE.fromRequest(request);
        ServiceDto saved = serviceService.create(dto);
        return ResponseEntity.ok(ServiceConverter.INSTANCE.toResponse(saved));
    }

    @PatchMapping(UPDATE_WITH_PATH_PARAMETER)
    @Operation(summary = "Partially update a service")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Service updated successfully"),
            @ApiResponse(responseCode = "404", description = "Service not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ServiceResponse> update(@PathVariable Long id, @RequestBody ServiceRequest request) {
        ServiceDto dto = ServiceConverter.INSTANCE.fromRequest(request);
        ServiceDto updated = serviceService.update(id, dto);
        if (ObjectUtils.isNull(updated)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ServiceConverter.INSTANCE.toResponse(updated));
    }

    @Operation(summary = "Delete a service by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Service deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Service not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping(DELETE_WITH_PATH_PARAMETER)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        serviceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
