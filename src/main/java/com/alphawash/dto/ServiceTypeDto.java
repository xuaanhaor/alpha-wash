package com.alphawash.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceTypeDto {
    private Long id;
    private String serviceTypeName;
}
