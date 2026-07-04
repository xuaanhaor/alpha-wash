package com.alphawash.service;

import com.alphawash.dto.ProductImportHistoryDto;
import com.alphawash.dto.ProductImportPreviewDto;
import com.alphawash.dto.ProductImportResultDto;
import java.io.InputStream;
import java.util.List;
import org.springframework.core.io.ByteArrayResource;

public interface ProductImportService {
    ByteArrayResource generateTemplate();

    ProductImportPreviewDto parseAndValidate(InputStream inputStream, String fileName);

    ProductImportResultDto importProducts(ProductImportPreviewDto preview, String importMode, String fileName);

    List<ProductImportHistoryDto> getImportHistory();

    ProductImportHistoryDto getImportHistoryById(Long id);

    ByteArrayResource generateErrorReport(ProductImportPreviewDto preview);
}
