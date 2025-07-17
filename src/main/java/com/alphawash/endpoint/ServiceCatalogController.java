package com.alphawash.endpoint;

import static com.alphawash.constant.Constant.*;

import com.alphawash.converter.ServiceCatalogConverter;
import com.alphawash.dto.ServiceCatalogDto;
import com.alphawash.request.ServiceCatalogRequest;
import com.alphawash.response.ServiceCatalogResponse;
import com.alphawash.service.ServiceCatalogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(API_SERVICE_CATALOG)
@RequiredArgsConstructor
@Tag(name = "ServiceCatalog", description = "Manage service pricing by size")
public class ServiceCatalogController {

    private final ServiceCatalogService service;

    @Operation(summary = "Get all service catalog entries")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list")
    @GetMapping(ROOT)
    public ResponseEntity<List<ServiceCatalogResponse>> getAll() {
        return ResponseEntity.ok(ServiceCatalogConverter.INSTANCE.toResponse(service.getAll()));
    }

    @Operation(summary = "Get service catalog by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Entry found"),
        @ApiResponse(responseCode = "404", description = "Entry not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(ID_PATH_PARAMETER)
    public ResponseEntity<ServiceCatalogResponse> getById(@PathVariable Long id) {
        ServiceCatalogDto dto = service.getById(id);
        return dto != null
                ? ResponseEntity.ok(ServiceCatalogConverter.INSTANCE.toResponse(dto))
                : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Create a new service catalog entry")
    @ApiResponse(responseCode = "200", description = "Created successfully")
    @PostMapping(INSERT_ENDPOINT)
    public ResponseEntity<ServiceCatalogResponse> create(@RequestBody ServiceCatalogRequest request) {
        ServiceCatalogDto dto = ServiceCatalogConverter.INSTANCE.fromRequest(request);
        ServiceCatalogDto saved = service.create(dto);
        return ResponseEntity.ok(ServiceCatalogConverter.INSTANCE.toResponse(saved));
    }

    @Operation(summary = "Update a service catalog entry")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Updated successfully"),
        @ApiResponse(responseCode = "404", description = "Entry not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PatchMapping(UPDATE_WITH_PATH_PARAMETER)
    public ResponseEntity<ServiceCatalogResponse> update(
            @PathVariable Long id, @RequestBody ServiceCatalogRequest request) {
        ServiceCatalogDto dto = ServiceCatalogConverter.INSTANCE.fromRequest(request);
        ServiceCatalogDto updated = service.update(id, dto);
        return updated != null
                ? ResponseEntity.ok(ServiceCatalogConverter.INSTANCE.toResponse(updated))
                : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Delete a service catalog entry")
    @ApiResponse(responseCode = "204", description = "Deleted successfully")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @DeleteMapping(DELETE_WITH_PATH_PARAMETER)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get service catalog entries by service ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Entries found"),
        @ApiResponse(responseCode = "404", description = "No entries found")
    })
    @GetMapping(API_SERVICE_CATEGORY + ID_PATH_PARAMETER)
    public ResponseEntity<List<ServiceCatalogResponse>> getByServiceId(@PathVariable Long id) {
        List<ServiceCatalogDto> dtos = service.getByServiceId(id);
        if (dtos.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ServiceCatalogConverter.INSTANCE.toResponse(dtos));
    }
}
