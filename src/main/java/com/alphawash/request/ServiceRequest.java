package com.alphawash.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceRequest {
    private String code;
    private String serviceName;
    private String duration;
    private String note;
    private Long serviceTypeId;
}
