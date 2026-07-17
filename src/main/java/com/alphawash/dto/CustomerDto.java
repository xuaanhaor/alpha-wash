package com.alphawash.dto;

import com.alphawash.constant.CustomerGender;
import com.alphawash.constant.CustomerStatus;
import java.time.LocalDate;
import java.util.UUID;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerDto {
    private UUID id;
    private String customerName;
    private String phone;
    private String note;
    private String email;
    private CustomerGender gender;
    private LocalDate birthday;
    private String address;
    private String avatarUrl;
    private CustomerStatus status;
}
