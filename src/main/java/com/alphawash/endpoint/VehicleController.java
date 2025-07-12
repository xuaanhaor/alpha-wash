package com.alphawash.endpoint;

import com.alphawash.constant.Constant;
import com.alphawash.dto.VehicleDto;
import com.alphawash.service.VehicleService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constant.API_VEHICLE)
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @GetMapping(Constant.SEARCH_ENDPOINT)
    public List<VehicleDto> search() {
        return vehicleService.search();
    }
}
