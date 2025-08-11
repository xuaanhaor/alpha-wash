package com.alphawash.dto;

import java.time.LocalDateTime;
import lombok.*;

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
    private LocalDateTime dateOfBirth;
    private String identityNumber;
    private LocalDateTime joinDate;
    private String workStatus;
    private String note;
}
