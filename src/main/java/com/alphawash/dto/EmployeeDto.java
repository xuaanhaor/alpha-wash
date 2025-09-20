package com.alphawash.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeDto {
    private Long id;
    private String name;
    private String phone;
    private String bankName;
    private String bankAccount;
    private LocalDate dateOfBirth;
    private String identityNumber;
    private LocalDate joinDate;
    private String workStatus;
    private String note;
}
