package com.alphawash.service.impl;

import com.alphawash.converter.CustomerConverter;
import com.alphawash.dto.CustomerDto;
import com.alphawash.entity.Customer;
import com.alphawash.repository.CustomerRepository;
import com.alphawash.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

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
        return customerRepository.findById(id)
                .map(CustomerConverter.INSTANCE::toDto)
                .orElse(null);
    }

    @Override
    public CustomerDto create(CustomerDto dto) {
        Customer saved = customerRepository.save(CustomerConverter.INSTANCE.toEntity(dto));
        return CustomerConverter.INSTANCE.toDto(saved);
    }

    @Override
    public CustomerDto update(UUID id, CustomerDto dto) {
        return customerRepository.findById(id).map(c -> {
            c.setCustomerName(dto.getCustomerName());
            c.setPhone(dto.getPhone());
            c.setNote(dto.getNote());
            return CustomerConverter.INSTANCE.toDto(customerRepository.save(c));
        }).orElse(null);
    }

    @Override
    public void delete(UUID id) {
        customerRepository.deleteById(id);
    }
}
