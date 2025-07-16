package com.alphawash.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
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
