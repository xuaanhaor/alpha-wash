package com.alphawash.dto;

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
}
