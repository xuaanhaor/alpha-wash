package com.alphawash.service;

import com.alphawash.dto.CustomerDto;
import com.alphawash.repository.CustomerListRepository;
import com.alphawash.request.CustomerCreateRequest;
import com.alphawash.request.CustomerUpdateRequest;
import com.alphawash.request.CustomerVehicleRequest;
import com.alphawash.response.CustomerDetailResponse;
import com.alphawash.response.CustomerInvoiceRowResponse;
import com.alphawash.response.CustomerListItemResponse;
import com.alphawash.response.CustomerStatsResponse;
import com.alphawash.response.CustomerVehicleResponse;
import com.alphawash.response.PageResponse;
import com.alphawash.response.VehicleSummaryResponse;
import java.util.List;
import java.util.UUID;

public interface CustomerService {
    List<CustomerDto> getAll();

    void delete(UUID id);

    List<CustomerVehicleResponse> findCustomerVehicleByPhoneOrLicensePlate(String phone);

    PageResponse<CustomerListItemResponse> list(CustomerListRepository.Criteria criteria, int page, int size);

    CustomerDetailResponse getDetail(UUID id);

    PageResponse<CustomerInvoiceRowResponse> getInvoices(UUID id, int page, int size);

    CustomerDetailResponse createCustomer(CustomerCreateRequest request);

    CustomerDetailResponse updateCustomer(UUID id, CustomerUpdateRequest request);

    VehicleSummaryResponse addVehicle(UUID customerId, CustomerVehicleRequest request);

    VehicleSummaryResponse updateVehicle(UUID customerId, UUID vehicleId, CustomerVehicleRequest request);

    void removeVehicle(UUID customerId, UUID vehicleId);

    CustomerStatsResponse recalculateStats(UUID id);

    byte[] exportCsv(CustomerListRepository.Criteria criteria, List<UUID> ids);
}
