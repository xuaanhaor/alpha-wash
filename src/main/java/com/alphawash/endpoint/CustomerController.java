package com.alphawash.endpoint;

import static com.alphawash.constant.Constant.*;

import com.alphawash.converter.CustomerConverter;
import com.alphawash.dto.CustomerDto;
import com.alphawash.request.CustomerRequest;
import com.alphawash.response.CustomerResponse;
import com.alphawash.service.CustomerService;
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
@RequestMapping(API_CUSTOMER)
@RequiredArgsConstructor
@Tag(name = "Customer", description = "Customer management endpoints")
public class CustomerController {

    private final CustomerService customerService;

    @Operation(summary = "Get all customers")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved customer list")
    @GetMapping(ROOT)
    public ResponseEntity<List<CustomerResponse>> getAll() {
        List<CustomerDto> dtos = customerService.getAll();
        return ResponseEntity.ok(CustomerConverter.INSTANCE.toResponse(dtos));
    }

    @Operation(summary = "Get customer by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customer found"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(ID_PATH_PARAMETER)
    public ResponseEntity<CustomerResponse> getById(@PathVariable UUID id) {
        CustomerDto dto = customerService.getById(id);
        return dto != null
                ? ResponseEntity.ok(CustomerConverter.INSTANCE.toResponse(dto))
                : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Create new customer")
    @ApiResponse(responseCode = "200", description = "Customer created successfully")
    @PostMapping(INSERT_ENDPOINT)
    public ResponseEntity<CustomerResponse> create(@RequestBody CustomerRequest request) {
        CustomerDto dto = CustomerConverter.INSTANCE.fromRequest(request);
        CustomerDto saved = customerService.create(dto);
        return ResponseEntity.ok(CustomerConverter.INSTANCE.toResponse(saved));
    }

    @Operation(summary = "Update existing customer")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customer updated successfully"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PatchMapping(UPDATE_WITH_PATH_PARAMETER)
    public ResponseEntity<CustomerResponse> update(
            @PathVariable UUID id, @RequestBody CustomerRequest request) {
        CustomerDto dto = CustomerConverter.INSTANCE.fromRequest(request);
        CustomerDto updated = customerService.update(id, dto);
        return updated != null
                ? ResponseEntity.ok(CustomerConverter.INSTANCE.toResponse(updated))
                : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Delete customer by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Customer deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping(DELETE_WITH_PATH_PARAMETER)
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        customerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
