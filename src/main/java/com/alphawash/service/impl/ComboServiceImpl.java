package com.alphawash.service.impl;

import com.alphawash.constant.SeqCode;
import com.alphawash.converter.ComboConverter;
import com.alphawash.dto.ComboCatalogDto;
import com.alphawash.dto.ServiceComboDto;
import com.alphawash.entity.ServiceCombo;
import com.alphawash.entity.ServiceComboDtl;
import com.alphawash.repository.ServiceComboDtlRepository;
import com.alphawash.repository.ServiceComboRepository;
import com.alphawash.request.CreateBasicComboRequest;
import com.alphawash.request.UpdateBasicComboRequest;
import com.alphawash.response.ComboModifiedResponse;
import com.alphawash.service.ComboService;
import com.alphawash.service.GenerateSeqService;
import com.alphawash.util.CollectionUtils;
import com.alphawash.util.ObjectUtils;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ComboServiceImpl implements ComboService {

    private final ServiceComboRepository comboRepository;
    private final ServiceComboDtlRepository comboDtlRepository;
    private final GenerateSeqService generateSeqService;

    @Override
    public List<ServiceComboDto> getAllCombos() {
        var codes = comboDtlRepository.findServiceCatalogCodes();
        if (CollectionUtils.isNotEmpty(codes)) {
            var codeMap = codes.stream()
                    .collect(Collectors.groupingBy(
                            ComboCatalogDto::getComboCode,
                            Collectors.mapping(ComboCatalogDto::getServiceCatalogCode, Collectors.toList())));
            var result = comboRepository.findAll();
            var toReturn = result.stream()
                    .peek(combo -> combo.setServiceCatalogCodes(codeMap.get(combo.getCode())))
                    .toList();
            return ComboConverter.INSTANCE.toDto(toReturn);
        }
        return new ArrayList<>();
    }

    @Override
    public ServiceComboDto getComboByCode(String code) {
        ServiceCombo combo = comboRepository
                .findByCode(code)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy mã combo: " + code));
        var dto = ComboConverter.INSTANCE.toDto(combo);
        dto.setServiceCatalogCodes(comboDtlRepository.findServiceCatalogCodesByComboCode(code));
        return dto;
    }

    @Override
    @Transactional
    public ComboModifiedResponse createCombo(CreateBasicComboRequest request) {
        String comboName = request.comboName();
        BigDecimal price = request.price();
        String note = request.note();
        List<String> serviceCatalogCodes = request.serviceCatalogCodes();

        String comboCode = generateSeqService.generateSeqCode(SeqCode.COMBO);

        ServiceCombo combo = ServiceCombo.builder()
                .code(comboCode)
                .comboName(comboName)
                .price(price)
                .note(note)
                .build();

        comboRepository.save(combo);

        List<ServiceComboDtl> comboDtl = Collections.emptyList();
        if (CollectionUtils.isNotEmpty(serviceCatalogCodes)) {
            List<String> uniqueCodes = serviceCatalogCodes.stream()
                    .distinct()
                    .filter(Objects::nonNull)
                    .filter(code -> !code.trim().isEmpty())
                    .toList();

            comboDtl = createComboDetails(comboCode, uniqueCodes);
        }
        return ComboModifiedResponse.builder()
                .comboCode(comboCode)
                .comboName(comboName)
                .price(price.toString())
                .serviceCatalogCodes(
                        CollectionUtils.isNotEmpty(comboDtl)
                                ? comboDtl.stream()
                                        .map(ServiceComboDtl::getServiceCatalogCode)
                                        .collect(Collectors.toList())
                                : Collections.emptyList())
                .build();
    }

    @Override
    @Transactional
    public ComboModifiedResponse updateCombo(UpdateBasicComboRequest request) {
        var comboCode = request.comboCode();
        var comboName = request.comboName();
        var price = request.price();
        var note = request.note();
        var serviceCatalogCodes = request.serviceCatalogCodes();

        var existingCombo = comboRepository
                .findByCode(comboCode)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy mã combo: " + comboCode));

        ObjectUtils.setIfNotNull(comboName, existingCombo::setComboName);
        ObjectUtils.setIfNotNull(price, existingCombo::setPrice);
        ObjectUtils.setIfNotNull(note, existingCombo::setNote);
        comboRepository.save(existingCombo);

        comboDtlRepository.deleteByComboCode(comboCode);
        if (CollectionUtils.isNotEmpty(serviceCatalogCodes)) {
            List<String> uniqueCodes = serviceCatalogCodes.stream()
                    .distinct()
                    .filter(Objects::nonNull)
                    .filter(code -> !code.trim().isEmpty())
                    .toList();

            createComboDetails(comboCode, uniqueCodes);
        }

        return ComboModifiedResponse.builder()
                .comboCode(existingCombo.getCode())
                .comboName(existingCombo.getComboName())
                .price(existingCombo.getPrice().toString())
                .serviceCatalogCodes(comboDtlRepository.findServiceCatalogCodesByComboCode(existingCombo.getCode()))
                .build();
    }

    @Override
    @Transactional
    public Integer deleteComboByCode(List<String> code) {
        if (CollectionUtils.isNotEmpty(code)) {
            int result = comboRepository.deleteByCodes(code);
            return result + comboDtlRepository.deleteByComboCodeIn(code);
        }
        return 0;
    }

    private List<ServiceComboDtl> createComboDetails(String comboCode, List<String> uniqueCodes) {
        if (uniqueCodes.isEmpty()) {
            return Collections.emptyList();
        }

        List<ServiceComboDtl> detailEntities = uniqueCodes.stream()
                .map(serviceCatalogCode -> ServiceComboDtl.builder()
                        .code(generateSeqService.generateSeqCode(SeqCode.COMBO_DETAIL))
                        .comboCode(comboCode)
                        .serviceCatalogCode(serviceCatalogCode)
                        .build())
                .toList();

        return comboDtlRepository.saveAll(detailEntities);
    }
}
