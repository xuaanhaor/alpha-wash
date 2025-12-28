package com.alphawash.service.impl;

import com.alphawash.repository.ServiceComboRepository;
import com.alphawash.response.ComboGetAllResponse;
import com.alphawash.service.ServiceComboService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ServiceComboImpl implements ServiceComboService {
    private final ServiceComboRepository serviceComboRepository;

    public List<ComboGetAllResponse> getAll() {

        List<Object[]> rows = serviceComboRepository.findAllComboFlatRaw();

        Map<String, ComboGetAllResponse> comboMap = new LinkedHashMap<>();

        for (Object[] r : rows) {

            String comboCode = (String) r[0];

            ComboGetAllResponse combo = comboMap.computeIfAbsent(
                    comboCode,
                    k -> ComboGetAllResponse.builder()
                            .comboCode(comboCode)
                            .comboName((String) r[1])
                            .durationDays(((Number) r[2]).intValue())
                            .status((String) r[3])
                            .catalogs(new ArrayList<>())
                            .build()
            );

            String catalogCode = (String) r[4];

            ComboGetAllResponse.Catalog catalog = combo.getCatalogs()
                    .stream()
                    .filter(c -> c.getCatalogCode().equals(catalogCode))
                    .findFirst()
                    .orElseGet(() -> {
                        ComboGetAllResponse.Catalog c =
                                ComboGetAllResponse.Catalog.builder()
                                        .catalogCode(catalogCode)
                                        .size((String) r[5])
                                        .price((BigDecimal) r[6])
                                        .priceIncludeTax((Boolean) r[7])
                                        .services(new ArrayList<>())
                                        .build();
                        combo.getCatalogs().add(c);
                        return c;
                    });

            catalog.getServices().add(
                    ComboGetAllResponse.ServiceQuota.builder()
                            .serviceCatalogCode((String) r[8])
                            .quantity(((Number) r[9]).intValue())
                            .build()
            );
        }

        return new ArrayList<>(comboMap.values());
    }
}
