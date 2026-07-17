package com.alphawash.endpoint;

import static com.alphawash.constant.Constant.*;

import com.alphawash.converter.CustomerConverter;
import com.alphawash.dto.CustomerDto;
import com.alphawash.repository.CustomerListRepository;
import com.alphawash.request.CustomerCreateRequest;
import com.alphawash.request.CustomerUpdateRequest;
import com.alphawash.request.CustomerVehicleRequest;
import com.alphawash.response.ApiResponse;
import com.alphawash.response.CustomerDetailResponse;
import com.alphawash.response.CustomerInvoiceRowResponse;
import com.alphawash.response.CustomerListItemResponse;
import com.alphawash.response.CustomerResponse;
import com.alphawash.response.CustomerStatsResponse;
import com.alphawash.response.CustomerVehicleResponse;
import com.alphawash.response.PageResponse;
import com.alphawash.response.VehicleSummaryResponse;
import com.alphawash.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(API_CUSTOMER)
@RequiredArgsConstructor
@Tag(name = "Customer", description = "Customer management endpoints")
public class CustomerController {

    private final CustomerService customerService;

    @Operation(summary = "Get all customers (legacy, unpaginated)")
    @GetMapping(ROOT)
    public ResponseEntity<ApiResponse<List<CustomerResponse>>> getAll() {
        List<CustomerDto> dtos = customerService.getAll();
        return ResponseEntity.ok(ApiResponse.success(CustomerConverter.INSTANCE.toResponse(dtos)));
    }

    @Operation(summary = "Search customers with pagination, filters and sorting")
    @GetMapping(LIST_ENDPOINT)
    public ResponseEntity<ApiResponse<PageResponse<CustomerListItemResponse>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String segment,
            @RequestParam(required = false) BigDecimal minSpending,
            @RequestParam(required = false) BigDecimal maxSpending,
            @RequestParam(required = false) Integer vehicleCount,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate lastVisitFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate lastVisitTo,
            @RequestParam(required = false, defaultValue = "lastVisitDate") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDir) {
        var criteria = new CustomerListRepository.Criteria(
                search, status, segment, minSpending, maxSpending, vehicleCount, lastVisitFrom, lastVisitTo, sortBy,
                sortDir);
        return ResponseEntity.ok(ApiResponse.success(customerService.list(criteria, page, size)));
    }

    @Operation(summary = "Get full customer profile with stats")
    @GetMapping(ID_PATH_PARAMETER)
    public ResponseEntity<ApiResponse<CustomerDetailResponse>> getDetail(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(customerService.getDetail(id)));
    }

    @Operation(summary = "Get paginated invoice history for a customer")
    @GetMapping(ID_PATH_PARAMETER + INVOICES_ENDPOINT)
    public ResponseEntity<ApiResponse<PageResponse<CustomerInvoiceRowResponse>>> getInvoices(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(customerService.getInvoices(id, page, size)));
    }

    @Operation(summary = "Create new customer, optionally with a vehicle")
    @PostMapping(INSERT_ENDPOINT)
    public ResponseEntity<ApiResponse<CustomerDetailResponse>> create(@RequestBody CustomerCreateRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Tạo khách hàng thành công", customerService.createCustomer(request)));
    }

    @Operation(summary = "Update existing customer")
    @PatchMapping(UPDATE_WITH_PATH_PARAMETER)
    public ResponseEntity<ApiResponse<CustomerDetailResponse>> update(
            @PathVariable UUID id, @RequestBody CustomerUpdateRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Cập nhật khách hàng thành công", customerService.updateCustomer(id, request)));
    }

    @Operation(summary = "Soft delete customer")
    @DeleteMapping(DELETE_WITH_PATH_PARAMETER)
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        customerService.delete(id);
        return ResponseEntity.ok(ApiResponse.<Void>success("Xóa khách hàng thành công", null));
    }

    @Operation(summary = "Add a vehicle to a customer")
    @PostMapping(ID_PATH_PARAMETER + VEHICLES_ENDPOINT)
    public ResponseEntity<ApiResponse<VehicleSummaryResponse>> addVehicle(
            @PathVariable UUID id, @RequestBody CustomerVehicleRequest request) {
        return ResponseEntity.ok(ApiResponse.success(customerService.addVehicle(id, request)));
    }

    @Operation(summary = "Update a vehicle owned by a customer")
    @PatchMapping(ID_PATH_PARAMETER + VEHICLES_ENDPOINT + VEHICLE_ID_PATH_PARAMETER)
    public ResponseEntity<ApiResponse<VehicleSummaryResponse>> updateVehicle(
            @PathVariable UUID id, @PathVariable UUID vehicleId, @RequestBody CustomerVehicleRequest request) {
        return ResponseEntity.ok(ApiResponse.success(customerService.updateVehicle(id, vehicleId, request)));
    }

    @Operation(summary = "Unlink a vehicle from a customer")
    @DeleteMapping(ID_PATH_PARAMETER + VEHICLES_ENDPOINT + VEHICLE_ID_PATH_PARAMETER)
    public ResponseEntity<ApiResponse<Void>> removeVehicle(@PathVariable UUID id, @PathVariable UUID vehicleId) {
        customerService.removeVehicle(id, vehicleId);
        return ResponseEntity.ok(ApiResponse.<Void>success("Đã gỡ xe khỏi khách hàng", null));
    }

    @Operation(summary = "Recalculate and return fresh stats for a customer (e.g. after a merge)")
    @PostMapping(ID_PATH_PARAMETER + RECALCULATE_STATS_ENDPOINT)
    public ResponseEntity<ApiResponse<CustomerStatsResponse>> recalculateStats(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(customerService.recalculateStats(id)));
    }

    @Operation(summary = "Export customers to CSV")
    @GetMapping(EXPORT_ENDPOINT)
    public ResponseEntity<byte[]> export(
            @RequestParam(defaultValue = "csv") String format,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String segment,
            @RequestParam(required = false) BigDecimal minSpending,
            @RequestParam(required = false) BigDecimal maxSpending,
            @RequestParam(required = false) Integer vehicleCount,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate lastVisitFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate lastVisitTo,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDir,
            @RequestParam(required = false) List<UUID> ids) {
        var criteria = new CustomerListRepository.Criteria(
                search, status, segment, minSpending, maxSpending, vehicleCount, lastVisitFrom, lastVisitTo, sortBy,
                sortDir);
        byte[] csv = customerService.exportCsv(criteria, ids);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"customers.csv\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(csv);
    }

    @Operation(summary = "Find customer + vehicles by phone or license plate")
    @GetMapping("/by-phone-or-plate")
    public ResponseEntity<ApiResponse<List<CustomerVehicleResponse>>> findCustomerVehicleByPhone(
            @RequestParam String phone) {
        return ResponseEntity.ok(
                ApiResponse.success(customerService.findCustomerVehicleByPhoneOrLicensePlate(phone)));
    }
}
