package com.alphawash.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeSkillDto {
    private Long id;
    private Long employeeId;
    private Long serviceId;
}
