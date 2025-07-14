package com.alphawash.endpoint;

import com.alphawash.converter.ServiceTypeConverter;
import com.alphawash.dto.ServiceTypeDto;
import com.alphawash.request.ServiceTypeRequest;
import com.alphawash.response.ServiceTypeResponse;
import com.alphawash.service.ServiceTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.alphawash.constant.Constant.*;

@RestController
@RequestMapping(API_SERVICE_TYPE)
@RequiredArgsConstructor
@Tag(name = "ServiceType", description = "Manage types of services (e.g. detailing, basic wash...)")
public class ServiceTypeController {

    private final ServiceTypeService service;

    @Operation(summary = "Get all service types")
    @ApiResponse(responseCode = "200", description = "Retrieved successfully")
    @GetMapping(ROOT)
    public ResponseEntity<List<ServiceTypeResponse>> getAll() {
        return ResponseEntity.ok(
                ServiceTypeConverter.INSTANCE.toResponse(service.getAll()));
    }

    @Operation(summary = "Get service type by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Found"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    @GetMapping(ID_PATH_PARAMETER)
    public ResponseEntity<ServiceTypeResponse> getById(@PathVariable Long id) {
        ServiceTypeDto dto = service.getById(id);
        return dto != null
                ? ResponseEntity.ok(ServiceTypeConverter.INSTANCE.toResponse(dto))
                : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Create a new service type")
    @ApiResponse(responseCode = "200", description = "Created successfully")
    @PostMapping(INSERT_ENDPOINT)
    public ResponseEntity<ServiceTypeResponse> create(@RequestBody ServiceTypeRequest request) {
        ServiceTypeDto dto = ServiceTypeConverter.INSTANCE.fromRequest(request);
        return ResponseEntity.ok(
                ServiceTypeConverter.INSTANCE.toResponse(service.create(dto)));
    }

    @Operation(summary = "Update a service type")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Updated successfully"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    @PatchMapping(UPDATE_WITH_PATH_PARAMETER)
    public ResponseEntity<ServiceTypeResponse> update(@PathVariable Long id, @RequestBody ServiceTypeRequest request) {
        ServiceTypeDto dto = ServiceTypeConverter.INSTANCE.fromRequest(request);
        ServiceTypeDto updated = service.update(id, dto);
        return updated != null
                ? ResponseEntity.ok(ServiceTypeConverter.INSTANCE.toResponse(updated))
                : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Delete a service type")
    @ApiResponse(responseCode = "204", description = "Deleted successfully")
    @DeleteMapping(DELETE_WITH_PATH_PARAMETER)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
