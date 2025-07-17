package com.alphawash.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceResponse {
    private Long id;
    private String serviceName;
    private String duration;
    private String note;
    private String serviceTypeCode;
}
