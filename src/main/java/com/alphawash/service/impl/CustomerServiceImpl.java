package com.alphawash.service.impl;

import com.alphawash.converter.CustomerConverter;
import com.alphawash.dto.CustomerDto;
import com.alphawash.entity.Customer;
import com.alphawash.repository.CustomerRepository;
import com.alphawash.service.CustomerService;
import java.util.List;
import java.util.UUID;

import com.alphawash.util.PatchHelper;
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
        return customerRepository.findById(id).map(existing -> {
            CustomerDto currentDto = CustomerConverter.INSTANCE.toDto(existing);
            PatchHelper.applyPatch(patchData, currentDto);
            Customer updated = CustomerConverter.INSTANCE.toEntity(currentDto);
            return CustomerConverter.INSTANCE.toDto(customerRepository.save(updated));
        }).orElse(null);
    }


    @Override
    public void delete(UUID id) {
        customerRepository.deleteById(id);
    }
}
