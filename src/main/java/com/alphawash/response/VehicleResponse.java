package com.alphawash.response;

import com.alphawash.dto.BrandDto;
import com.alphawash.dto.ModelWithoutBrandDto;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleResponse {
    private UUID id;
    private String licensePlate;
    private BrandDto brand;
    private ModelWithoutBrandDto model;
    private String imageUrl;
    private String note;
}
