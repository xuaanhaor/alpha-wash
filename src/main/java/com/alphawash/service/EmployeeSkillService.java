package com.alphawash.service;

import com.alphawash.dto.EmployeeSkillDto;

import java.util.List;

public interface EmployeeSkillService {
    List<EmployeeSkillDto> getAll();
    EmployeeSkillDto getById(Long id);
    EmployeeSkillDto create(EmployeeSkillDto dto);
    EmployeeSkillDto update(Long id, EmployeeSkillDto dto);
    void delete(Long id);
}
