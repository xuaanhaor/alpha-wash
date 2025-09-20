package com.alphawash.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeResponse {
    Long id;
    String name;
    String phone;
    String bankName;
    String bankAccount;
    LocalDateTime dateOfBirth;
    String identityNumber;
    LocalDateTime joinDate;
    String workStatus;
    String note;
}
