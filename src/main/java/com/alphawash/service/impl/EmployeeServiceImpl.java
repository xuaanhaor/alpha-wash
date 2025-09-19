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

import java.time.LocalDate;
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
            if (request.phone() != null) {
                boolean exists = employeeRepository.existsByPhone(request.phone());
                if (exists) {
                    throw new BusinessException(HttpStatus.CONFLICT, "Số điện thoại đã tồn tại");
                }
            }

            //Check trùng CCCD
            if (request.identityNumber() != null) {
                boolean exists = employeeRepository.existsByIdentityNumber(request.identityNumber());
                if (exists) {
                    throw new BusinessException(HttpStatus.CONFLICT, "Số căn cước công dân đã tồn tại");
                }
            }
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
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Tạo mới nhân viên thất bại: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public EmployeeDto update(Long id, EmployeeRequest request) {
        var employee = employeeRepository
                .findById(id)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, ErrorConst.E004.formatted(id)));

        //Check trùng phone
        if (request.phone() != null) {
            boolean exists = employeeRepository.existsDuplicatePhone(request.phone(), id);
            if (exists) {
                throw new BusinessException(HttpStatus.CONFLICT, "Số điện thoại đã tồn tại");
            }
        }

        //Check trùng CCCD
        if (request.identityNumber() != null) {
            boolean exists = employeeRepository.existsByIdentityNumberAndIdNot(request.identityNumber(), id);
            if (exists) {
                throw new BusinessException(HttpStatus.CONFLICT, "Số căn cước công dân đã tồn tại");
            }
        }
        var result = updateEmployee(request, employee);
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
            throw new BusinessException(HttpStatus.CONFLICT, "Cập nhật nhân viên thất bại: " + e.getMessage());
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
