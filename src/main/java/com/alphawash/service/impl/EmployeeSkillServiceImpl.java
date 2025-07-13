package com.alphawash.service.impl;

import com.alphawash.converter.EmployeeSkillConverter;
import com.alphawash.dto.EmployeeSkillDto;
import com.alphawash.entity.EmployeeSkill;
import com.alphawash.repository.EmployeeRepository;
import com.alphawash.repository.EmployeeSkillRepository;
import com.alphawash.repository.ServiceRepository;
import com.alphawash.service.EmployeeSkillService;
import com.alphawash.util.PatchHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeSkillServiceImpl implements EmployeeSkillService {

    private final EmployeeSkillRepository employeeSkillRepository;
    private final EmployeeRepository employeeRepository;
    private final ServiceRepository serviceRepository;
    private final EmployeeSkillConverter converter = EmployeeSkillConverter.INSTANCE;

    @Override
    public List<EmployeeSkillDto> getAll() {
        return converter.toDto(employeeSkillRepository.findAll());
    }

    @Override
    public EmployeeSkillDto getById(Long id) {
        return employeeSkillRepository.findById(id)
                .map(converter::toDto)
                .orElse(null);
    }

    @Override
    public EmployeeSkillDto create(EmployeeSkillDto dto) {
        EmployeeSkill entity = converter.toEntity(dto);
        entity.setEmployee(employeeRepository.findById(dto.getEmployeeId()).orElseThrow());
        entity.setService(serviceRepository.findById(dto.getServiceId()).orElseThrow());
        return converter.toDto(employeeSkillRepository.save(entity));
    }

    @Override
    public EmployeeSkillDto update(Long id, EmployeeSkillDto patchData) {
        return employeeSkillRepository.findById(id).map(existing -> {
            EmployeeSkillDto currentDto = converter.toDto(existing);
            PatchHelper.applyPatch(patchData, currentDto);
            EmployeeSkill updated = converter.toEntity(currentDto);
            updated.setEmployee(employeeRepository.findById(currentDto.getEmployeeId()).orElse(null));
            updated.setService(serviceRepository.findById(currentDto.getServiceId()).orElse(null));
            return converter.toDto(employeeSkillRepository.save(updated));
        }).orElse(null);
    }

    @Override
    public void delete(Long id) {
        employeeSkillRepository.deleteById(id);
    }
}
