package com.alphawash.converter;

import com.alphawash.dto.EmployeeSkillDto;
import com.alphawash.entity.EmployeeSkill;
import com.alphawash.request.EmployeeSkillRequest;
import com.alphawash.response.EmployeeSkillResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface EmployeeSkillConverter {
    EmployeeSkillConverter INSTANCE = Mappers.getMapper(EmployeeSkillConverter.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employeeId", source = "employeeId")
    @Mapping(target = "serviceId", source = "serviceId")
    EmployeeSkillDto fromRequest(EmployeeSkillRequest request);

    @Mapping(target = "employeeId", source = "employee.id")
    @Mapping(target = "serviceId", source = "service.id")
    EmployeeSkillDto toDto(EmployeeSkill entity);

    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "service", ignore = true)
    EmployeeSkill toEntity(EmployeeSkillDto dto);

    EmployeeSkillResponse toResponse(EmployeeSkillDto dto);
    List<EmployeeSkillDto> toDto(List<EmployeeSkill> entities);
    List<EmployeeSkillResponse> toResponse(List<EmployeeSkillDto> dtos);
}
