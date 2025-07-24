package com.alphawash.service;

import com.alphawash.dto.CustomerDto;
import com.alphawash.response.CustomerVehicleResponse;
import java.util.List;
import java.util.UUID;

public interface CustomerService {
    List<CustomerDto> getAll();

    CustomerDto getById(UUID id);

    CustomerDto create(CustomerDto dto);

    CustomerDto update(UUID id, CustomerDto dto);

    void delete(UUID id);

    CustomerVehicleResponse findByPhone(String phone);

    CustomerVehicleResponse findCustomerVehicleByPhoneOrLicensePlate(String phone);
}
