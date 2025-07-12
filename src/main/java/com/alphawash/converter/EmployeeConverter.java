package com.alphawash.converter;

import com.alphawash.dto.EmployeeDto;
import com.alphawash.entity.Employee;
import com.alphawash.request.EmployeeRequest;
import com.alphawash.response.EmployeeResponse;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface EmployeeConverter {
    EmployeeConverter INSTANCE = Mappers.getMapper(EmployeeConverter.class);

    // Entity <-> DTO
    EmployeeDto toDto(Employee employee);

    Employee toEntity(EmployeeDto dto);

    // Request -> DTO
    EmployeeDto fromRequest(EmployeeRequest request);

    // DTO -> Response
    EmployeeResponse toResponse(EmployeeDto dto);

    // Danh sách
    List<EmployeeDto> toDto(List<Employee> employees);

    List<EmployeeResponse> toResponse(List<EmployeeDto> dtos);
}
