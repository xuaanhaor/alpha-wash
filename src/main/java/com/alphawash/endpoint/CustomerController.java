package com.alphawash.controller;

import static com.alphawash.constant.Constant.*;

import com.alphawash.converter.CustomerConverter;
import com.alphawash.dto.CustomerDto;
import com.alphawash.request.CustomerRequest;
import com.alphawash.response.ApiResponse;
import com.alphawash.response.CustomerResponse;
import com.alphawash.service.CustomerService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(API_CUSTOMER)
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping(ROOT)
    public ResponseEntity<ApiResponse<List<CustomerResponse>>> getAll() {
        List<CustomerDto> dtos = customerService.getAll();
        return ResponseEntity.ok(ApiResponse.success(CustomerConverter.INSTANCE.toResponse(dtos)));
    }

    @GetMapping(ID_PATH_PARAMETER)
    public ResponseEntity<ApiResponse<CustomerResponse>> getById(@PathVariable UUID id) {
        CustomerDto dto = customerService.getById(id);
        return dto != null
                ? ResponseEntity.ok(ApiResponse.success(CustomerConverter.INSTANCE.toResponse(dto)))
                : ResponseEntity.notFound().build();
    }

    @PostMapping(INSERT_ENDPOINT)
    public ResponseEntity<ApiResponse<CustomerResponse>> create(@RequestBody CustomerRequest request) {
        CustomerDto dto = CustomerConverter.INSTANCE.fromRequest(request);
        CustomerDto saved = customerService.create(dto);
        return ResponseEntity.ok(ApiResponse.success(CustomerConverter.INSTANCE.toResponse(saved)));
    }

    @PatchMapping(UPDATE_WITH_PATH_PARAMETER)
    public ResponseEntity<ApiResponse<CustomerResponse>> update(
            @PathVariable UUID id, @RequestBody CustomerRequest request) {
        CustomerDto dto = CustomerConverter.INSTANCE.fromRequest(request);
        CustomerDto updated = customerService.update(id, dto);
        return updated != null
                ? ResponseEntity.ok(ApiResponse.success(CustomerConverter.INSTANCE.toResponse(updated)))
                : ResponseEntity.notFound().build();
    }

    @DeleteMapping(DELETE_WITH_PATH_PARAMETER)
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        customerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
