package com.alphawash.service.impl;

import com.alphawash.constant.ErrorConst;
import com.alphawash.converter.EmployeeConverter;
import com.alphawash.dto.EmployeeDto;
import com.alphawash.entity.Employee;
import com.alphawash.exception.BusinessException;
import com.alphawash.repository.EmployeeRepository;
import com.alphawash.request.EmployeeRequest;
import com.alphawash.service.EmployeeService;
import com.alphawash.util.ObjectUtils;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    @Transactional
    public EmployeeDto create(EmployeeRequest request) {
        try {
            Employee saved = employeeRepository.save(Employee.builder()
                    .name(request.name())
                    .phone(request.phone())
                    .bankName(request.bankName())
                    .bankAccount(request.bankAccount())
                    .dateOfBirth(request.dateOfBirth())
                    .identityNumber(request.identityNumber())
                    .joinDate(request.joinDate())
                    .workStatus(request.workStatus())
                    .note(request.note())
                    .build());
            return employeeConverter.toDto(saved);
        } catch (Exception e) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Failed to create new employee: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public EmployeeDto update(Long id, EmployeeRequest request) {
        var employeeOptional = employeeRepository
                .findById(id)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, ErrorConst.E004.formatted(id)));
        var result = updateEmployee(request, employeeOptional);
        return employeeConverter.toDto(result);
    }

    private Employee updateEmployee(EmployeeRequest request, Employee employee) {
        try {
            ObjectUtils.setIfNotNull(request.name(), employee::setName);
            ObjectUtils.setIfNotNull(request.phone(), employee::setPhone);
            ObjectUtils.setIfNotNull(request.bankName(), employee::setBankName);
            ObjectUtils.setIfNotNull(request.bankAccount(), employee::setBankAccount);
            ObjectUtils.setIfNotNull(request.dateOfBirth(), employee::setDateOfBirth);
            ObjectUtils.setIfNotNull(request.identityNumber(), employee::setIdentityNumber);
            ObjectUtils.setIfNotNull(request.joinDate(), employee::setJoinDate);
            ObjectUtils.setIfNotNull(request.workStatus(), employee::setWorkStatus);
            ObjectUtils.setIfNotNull(request.note(), employee::setNote);
            return employeeRepository.save(employee);
        } catch (Exception e) {
            throw new BusinessException(HttpStatus.CONFLICT, "Failed to update employee: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        try {
            employeeRepository.deleteById(id);
        } catch (Exception e) {
            throw new BusinessException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete employee: " + e.getMessage());
        }
    }
}
