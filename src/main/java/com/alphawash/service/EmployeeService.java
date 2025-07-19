package com.alphawash.service;

import com.alphawash.dto.EmployeeDto;
import com.alphawash.request.EmployeeRequest;
import java.util.List;

public interface EmployeeService {
    List<EmployeeDto> getAll();

    EmployeeDto getById(Long id);

    EmployeeDto create(EmployeeRequest request);

    EmployeeDto update(Long id, EmployeeRequest request);

    void delete(Long id);
}
