package com.alphawash.service.impl;

import com.alphawash.converter.CustomerConverter;
import com.alphawash.dto.CustomerDto;
import com.alphawash.dto.CustomerVehicleDto;
import com.alphawash.dto.CustomerVehicleFlatDto;
import com.alphawash.entity.Customer;
import com.alphawash.repository.CustomerRepository;
import com.alphawash.response.CustomerVehicleResponse;
import com.alphawash.service.CustomerService;
import com.alphawash.util.CollectionUtils;
import com.alphawash.util.ObjectUtils;
import com.alphawash.util.PatchHelper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    public List<CustomerDto> getAll() {
        return CustomerConverter.INSTANCE.toDto(customerRepository.findAll());
    }

    @Override
    public CustomerDto getById(UUID id) {
        return customerRepository
                .findById(id)
                .map(CustomerConverter.INSTANCE::toDto)
                .orElse(null);
    }

    @Override
    public CustomerDto create(CustomerDto dto) {
        Customer saved = customerRepository.save(CustomerConverter.INSTANCE.toEntity(dto));
        return CustomerConverter.INSTANCE.toDto(saved);
    }

    @Override
    public CustomerDto update(UUID id, CustomerDto patchData) {
        return customerRepository
                .findById(id)
                .map(existing -> {
                    CustomerDto currentDto = CustomerConverter.INSTANCE.toDto(existing);
                    PatchHelper.applyPatch(patchData, currentDto);
                    Customer updated = CustomerConverter.INSTANCE.toEntity(currentDto);
                    return CustomerConverter.INSTANCE.toDto(customerRepository.save(updated));
                })
                .orElse(null);
    }

    @Override
    public void delete(UUID id) {
        customerRepository.deleteById(id);
    }

    @Override
    public CustomerVehicleResponse findByPhone(String phone) {
        Optional<Customer> result = customerRepository.findByPhone(phone);
        return result.map(CustomerConverter.INSTANCE::toCustomerVehicleResp).orElse(null);
    }

    @Override
    public CustomerVehicleResponse findCustomerVehicleByPhone(String phone) {
        List<CustomerVehicleFlatDto> flatList = customerRepository.findCustomerWithVehicleByPhone(phone);

        //        Map<UUID, CustomerVehicleResponse> vehicleMap = new LinkedHashMap<>();
        //
        //        for (CustomerVehicleFlatDto flat : flatList) {
        //            vehicleMap.computeIfAbsent(flat.getId(), id -> new CustomerVehicleResponse(
        //                    flat.getId(),
        //                    flat.getCustomerName(),
        //                    flat.getPhone(),
        //                    new ArrayList<>()
        //            )).getVehicles().add(new CustomerVehicleDto(
        //                    flat.getBrandCode(),
        //                    flat.getBrandName(),
        //                    flat.getModelCode(),
        //                    flat.getModelName(),
        //                    flat.getLicensePlate()
        //            ));
        //        }
        //
        //        return new ArrayList<>(grouped.values());

        if (CollectionUtils.isNotEmpty(flatList)) {
            var first = flatList.get(0);
            List<CustomerVehicleDto> vehicles = flatList.stream()
                    .filter(item -> ObjectUtils.isNotNull(item.getLicensePlate()))
                    .map(flat -> new CustomerVehicleDto(
                            flat.getBrandCode(),
                            flat.getBrandName(),
                            flat.getModelCode(),
                            flat.getModelName(),
                            flat.getLicensePlate()))
                    .toList();
            return CustomerVehicleResponse.builder()
                    .customerId(first.getId())
                    .customerName(first.getCustomerName())
                    .phone(first.getPhone())
                    .vehicles(vehicles)
                    .build();
        }

        return null;
    }
}
