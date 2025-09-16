package com.alphawash.service;

import com.alphawash.dto.ServiceComboDto;
import com.alphawash.request.CreateBasicComboRequest;
import com.alphawash.request.UpdateBasicComboRequest;
import com.alphawash.response.ComboModifiedResponse;
import java.util.List;

public interface ComboService {
    List<ServiceComboDto> getAllCombos();

    ServiceComboDto getComboByCode(String code);

    ComboModifiedResponse createCombo(CreateBasicComboRequest request);

    ComboModifiedResponse updateCombo(UpdateBasicComboRequest request);

    Integer deleteComboByCode(List<String> code);
}
