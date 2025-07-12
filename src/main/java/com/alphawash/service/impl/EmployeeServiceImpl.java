package com.alphawash.service.impl;

import com.alphawash.converter.EmployeeConverter;
import com.alphawash.dto.EmployeeDto;
import com.alphawash.entity.Employee;
import com.alphawash.repository.EmployeeRepository;
import com.alphawash.service.EmployeeService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeConverter employeeConverter;

    @Override
    public List<EmployeeDto> getAll() {
        List<Employee> employees = employeeRepository.findAll();
        return employeeConverter.toDto(employees);
    }

    @Override
    public EmployeeDto getById(Long id) {
        return employeeRepository.findById(id).map(employeeConverter::toDto).orElse(null);
    }

    @Override
    public EmployeeDto create(EmployeeDto dto) {
        Employee saved = employeeRepository.save(employeeConverter.toEntity(dto));
        return employeeConverter.toDto(saved);
    }

    @Override
    public EmployeeDto update(Long id, EmployeeDto dto) {
        return employeeRepository
                .findById(id)
                .map(existing -> {
                    existing.setName(dto.getName());
                    existing.setPhone(dto.getPhone());
                    existing.setNote(dto.getNote());
                    return employeeConverter.toDto(employeeRepository.save(existing));
                })
                .orElse(null);
    }

    @Override
    public void delete(Long id) {
        employeeRepository.deleteById(id);
    }
}
