package com.alphawash.response;

import java.util.UUID;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerResponse {
    private UUID id;
    private String customerName;
    private String phone;
    private String note;
}
