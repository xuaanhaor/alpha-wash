package com.alphawash.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeSkillResponse {
    private Long id;
    private Long employeeId;
    private Long serviceId;
}
