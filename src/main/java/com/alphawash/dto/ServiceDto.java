package com.alphawash.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceDto {
    private Long id;
    private String code;
    private String serviceName;
    private String duration;
    private String note;
    private String serviceTypeCode;
}
