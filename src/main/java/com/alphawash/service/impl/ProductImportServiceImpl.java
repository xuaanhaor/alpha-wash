package com.alphawash.service.impl;

import com.alphawash.dto.*;
import com.alphawash.entity.Product;
import com.alphawash.entity.ProductCategory;
import com.alphawash.entity.ProductImportHistory;
import com.alphawash.entity.Supplier;
import com.alphawash.repository.*;
import com.alphawash.service.ProductImportService;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductImportServiceImpl implements ProductImportService {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;
    private final ProductImportHistoryRepository importHistoryRepository;

    private static final String[] HEADERS = {
            "Mã sản phẩm", "Tên sản phẩm (*)", "Danh mục (*)", "Thương hiệu",
            "Mô tả", "Đơn vị (*)", "Giá nhập (*)", "Giá bán (*)",
            "Giá bán tối thiểu", "Barcode", "Tồn kho hiện tại", "Tồn kho tối thiểu",
            "Vị trí/Kệ", "Nhà cung cấp", "Trạng thái (Active/Inactive)"
    };

    @Override
    public ByteArrayResource generateTemplate() {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("Products");

            // Header style - required fields highlighted
            XSSFCellStyle requiredHeaderStyle = workbook.createCellStyle();
            requiredHeaderStyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
            requiredHeaderStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            XSSFFont boldFont = workbook.createFont();
            boldFont.setBold(true);
            requiredHeaderStyle.setFont(boldFont);
            requiredHeaderStyle.setBorderBottom(BorderStyle.THIN);
            requiredHeaderStyle.setBorderTop(BorderStyle.THIN);
            requiredHeaderStyle.setBorderLeft(BorderStyle.THIN);
            requiredHeaderStyle.setBorderRight(BorderStyle.THIN);

            XSSFCellStyle normalHeaderStyle = workbook.createCellStyle();
            normalHeaderStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
            normalHeaderStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            normalHeaderStyle.setFont(boldFont);
            normalHeaderStyle.setBorderBottom(BorderStyle.THIN);
            normalHeaderStyle.setBorderTop(BorderStyle.THIN);
            normalHeaderStyle.setBorderLeft(BorderStyle.THIN);
            normalHeaderStyle.setBorderRight(BorderStyle.THIN);

            // Instructions row
            Row instructionRow = sheet.createRow(0);
            Cell instrCell = instructionRow.createCell(0);
            instrCell.setCellValue("Hướng dẫn: Các cột có dấu (*) là bắt buộc. Trạng thái: Active hoặc Inactive. Danh mục và Nhà cung cấp phải tồn tại trong hệ thống.");
            XSSFCellStyle instrStyle = workbook.createCellStyle();
            XSSFFont instrFont = workbook.createFont();
            instrFont.setItalic(true);
            instrFont.setColor(IndexedColors.BLUE.getIndex());
            instrStyle.setFont(instrFont);
            instrCell.setCellStyle(instrStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 14));

            // Header row
            Row headerRow = sheet.createRow(1);
            int[] requiredCols = {1, 2, 5, 6, 7};
            Set<Integer> requiredSet = new HashSet<>();
            for (int col : requiredCols) requiredSet.add(col);

            for (int i = 0; i < HEADERS.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(HEADERS[i]);
                cell.setCellStyle(requiredSet.contains(i) ? requiredHeaderStyle : normalHeaderStyle);
                sheet.setColumnWidth(i, 5000);
            }

            // Sample data row
            Row sampleRow = sheet.createRow(2);
            String[] sampleData = {
                    "", "Dung dịch rửa xe Sonax 1L", "Dung dịch rửa xe", "Sonax",
                    "Dung dịch rửa xe cao cấp từ Đức", "chai", "180000", "350000",
                    "280000", "4064700123456", "50", "10",
                    "Kệ A1", "Sonax Vietnam", "Active"
            };
            XSSFCellStyle sampleStyle = workbook.createCellStyle();
            XSSFFont sampleFont = workbook.createFont();
            sampleFont.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
            sampleStyle.setFont(sampleFont);
            for (int i = 0; i < sampleData.length; i++) {
                Cell cell = sampleRow.createCell(i);
                cell.setCellValue(sampleData[i]);
                cell.setCellStyle(sampleStyle);
            }

            // Categories reference sheet
            XSSFSheet catSheet = workbook.createSheet("Danh mục tham chiếu");
            Row catHeader = catSheet.createRow(0);
            catHeader.createCell(0).setCellValue("Mã");
            catHeader.createCell(1).setCellValue("Tên danh mục");
            List<ProductCategory> categories = categoryRepository.findByIsActiveTrueAndDeleteFlagFalseOrderByDisplayOrderAsc();
            for (int i = 0; i < categories.size(); i++) {
                Row row = catSheet.createRow(i + 1);
                row.createCell(0).setCellValue(categories.get(i).getCode());
                row.createCell(1).setCellValue(categories.get(i).getCategoryName());
            }
            catSheet.setColumnWidth(0, 4000);
            catSheet.setColumnWidth(1, 8000);

            // Suppliers reference sheet
            XSSFSheet supSheet = workbook.createSheet("NCC tham chiếu");
            Row supHeader = supSheet.createRow(0);
            supHeader.createCell(0).setCellValue("Mã");
            supHeader.createCell(1).setCellValue("Tên NCC");
            List<Supplier> suppliers = supplierRepository.findByIsActiveTrueAndDeleteFlagFalseOrderBySupplierNameAsc();
            for (int i = 0; i < suppliers.size(); i++) {
                Row row = supSheet.createRow(i + 1);
                row.createCell(0).setCellValue(suppliers.get(i).getCode());
                row.createCell(1).setCellValue(suppliers.get(i).getSupplierName());
            }
            supSheet.setColumnWidth(0, 4000);
            supSheet.setColumnWidth(1, 8000);

            // Protect structure
            sheet.protectSheet("");
            sheet.lockSelectLockedCells(false);
            sheet.lockSelectUnlockedCells(false);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return new ByteArrayResource(out.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate template", e);
        }
    }

    @Override
    public ProductImportPreviewDto parseAndValidate(InputStream inputStream, String fileName) {
        List<ProductImportRowDto> rows = new ArrayList<>();

        // Load reference data
        Map<String, String> categoryMap = new HashMap<>();
        categoryRepository.findByIsActiveTrueAndDeleteFlagFalseOrderByDisplayOrderAsc()
                .forEach(c -> {
                    categoryMap.put(c.getCategoryName().toLowerCase(), c.getCode());
                    categoryMap.put(c.getCode().toLowerCase(), c.getCode());
                });

        Map<String, String> supplierMap = new HashMap<>();
        supplierRepository.findByIsActiveTrueAndDeleteFlagFalseOrderBySupplierNameAsc()
                .forEach(s -> {
                    supplierMap.put(s.getSupplierName().toLowerCase(), s.getCode());
                    supplierMap.put(s.getCode().toLowerCase(), s.getCode());
                });

        Set<String> existingBarcodes = new HashSet<>();
        productRepository.findByDeleteFlagFalseOrderByProductNameAsc()
                .forEach(p -> { if (p.getBarcode() != null) existingBarcodes.add(p.getBarcode()); });

        Set<String> existingCodes = new HashSet<>();
        productRepository.findByDeleteFlagFalseOrderByProductNameAsc()
                .forEach(p -> existingCodes.add(p.getCode()));

        Set<String> seenBarcodes = new HashSet<>();
        Set<String> seenCodes = new HashSet<>();

        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            int startRow = findDataStartRow(sheet);

            for (int i = startRow; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isRowEmpty(row)) continue;

                ProductImportRowDto rowDto = parseRow(row, i + 1);
                List<String> errors = validateRow(rowDto, categoryMap, supplierMap,
                        existingBarcodes, existingCodes, seenBarcodes, seenCodes);
                rowDto.setErrors(errors);
                rowDto.setValid(errors.isEmpty());
                rows.add(rowDto);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Excel file: " + e.getMessage(), e);
        }

        int valid = (int) rows.stream().filter(ProductImportRowDto::isValid).count();
        return ProductImportPreviewDto.builder()
                .totalRows(rows.size())
                .validRows(valid)
                .invalidRows(rows.size() - valid)
                .rows(rows)
                .build();
    }

    @Override
    @Transactional
    public ProductImportResultDto importProducts(ProductImportPreviewDto preview, String importMode, String fileName) {
        int imported = 0, updated = 0, skipped = 0, failed = 0;
        List<ProductImportRowDto> failedDetails = new ArrayList<>();

        Map<String, String> categoryMap = new HashMap<>();
        categoryRepository.findByIsActiveTrueAndDeleteFlagFalseOrderByDisplayOrderAsc()
                .forEach(c -> {
                    categoryMap.put(c.getCategoryName().toLowerCase(), c.getCode());
                    categoryMap.put(c.getCode().toLowerCase(), c.getCode());
                });

        Map<String, String> supplierMap = new HashMap<>();
        supplierRepository.findByIsActiveTrueAndDeleteFlagFalseOrderBySupplierNameAsc()
                .forEach(s -> {
                    supplierMap.put(s.getSupplierName().toLowerCase(), s.getCode());
                    supplierMap.put(s.getCode().toLowerCase(), s.getCode());
                });

        for (ProductImportRowDto row : preview.getRows()) {
            if (!row.isValid()) {
                failed++;
                failedDetails.add(row);
                continue;
            }

            try {
                Product existing = findExisting(row);

                if (existing != null) {
                    switch (importMode) {
                        case "SKIP_DUPLICATES" -> { skipped++; continue; }
                        case "UPDATE_EXISTING" -> {
                            updateProduct(existing, row, categoryMap, supplierMap);
                            productRepository.save(existing);
                            updated++;
                        }
                        case "CREATE_ONLY" -> { skipped++; continue; }
                        default -> { skipped++; continue; }
                    }
                } else {
                    Product newProduct = createProduct(row, categoryMap, supplierMap);
                    productRepository.save(newProduct);
                    imported++;
                }
            } catch (Exception e) {
                failed++;
                row.setErrors(List.of("Lỗi hệ thống: " + e.getMessage()));
                row.setValid(false);
                failedDetails.add(row);
            }
        }

        // Save history
        ProductImportHistory history = ProductImportHistory.builder()
                .fileName(fileName)
                .importedBy("admin")
                .importedAt(LocalDateTime.now())
                .totalRows(preview.getTotalRows())
                .successRows(imported)
                .failedRows(failed)
                .updatedRows(updated)
                .skippedRows(skipped)
                .status("COMPLETED")
                .importMode(importMode)
                .build();
        ProductImportHistory savedHistory = importHistoryRepository.save(history);

        return ProductImportResultDto.builder()
                .importHistoryId(savedHistory.getId())
                .totalRows(preview.getTotalRows())
                .importedRows(imported)
                .updatedRows(updated)
                .skippedRows(skipped)
                .failedRows(failed)
                .failedDetails(failedDetails)
                .build();
    }

    @Override
    public List<ProductImportHistoryDto> getImportHistory() {
        return importHistoryRepository.findByDeleteFlagFalseOrderByImportedAtDesc().stream()
                .map(this::toHistoryDto)
                .toList();
    }

    @Override
    public ProductImportHistoryDto getImportHistoryById(Long id) {
        return importHistoryRepository.findById(id).map(this::toHistoryDto).orElse(null);
    }

    @Override
    public ByteArrayResource generateErrorReport(ProductImportPreviewDto preview) {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("Errors");

            XSSFCellStyle headerStyle = workbook.createCellStyle();
            XSSFFont boldFont = workbook.createFont();
            boldFont.setBold(true);
            headerStyle.setFont(boldFont);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Row headerRow = sheet.createRow(0);
            String[] errorHeaders = {"Dòng", "Tên sản phẩm", "Trạng thái", "Lỗi"};
            for (int i = 0; i < errorHeaders.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(errorHeaders[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            XSSFCellStyle errorStyle = workbook.createCellStyle();
            XSSFFont redFont = workbook.createFont();
            redFont.setColor(IndexedColors.RED.getIndex());
            errorStyle.setFont(redFont);

            for (ProductImportRowDto row : preview.getRows()) {
                if (!row.isValid()) {
                    Row r = sheet.createRow(rowIdx++);
                    r.createCell(0).setCellValue(row.getRowNumber());
                    r.createCell(1).setCellValue(row.getProductName() != null ? row.getProductName() : "");
                    Cell statusCell = r.createCell(2);
                    statusCell.setCellValue("Lỗi");
                    statusCell.setCellStyle(errorStyle);
                    r.createCell(3).setCellValue(String.join("; ", row.getErrors()));
                }
            }

            sheet.setColumnWidth(0, 2000);
            sheet.setColumnWidth(1, 8000);
            sheet.setColumnWidth(2, 3000);
            sheet.setColumnWidth(3, 15000);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return new ByteArrayResource(out.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate error report", e);
        }
    }

    // --- Private helpers ---

    private int findDataStartRow(Sheet sheet) {
        for (int i = 0; i <= Math.min(5, sheet.getLastRowNum()); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            Cell firstCell = row.getCell(0);
            if (firstCell != null) {
                String val = getCellStringValue(firstCell).toLowerCase();
                if (val.contains("mã sản phẩm") || val.contains("product code")) {
                    return i + 1;
                }
            }
        }
        return 2; // Default: skip instruction + header
    }

    private boolean isRowEmpty(Row row) {
        for (int i = 0; i < 15; i++) {
            Cell cell = row.getCell(i);
            if (cell != null && !getCellStringValue(cell).isBlank()) return false;
        }
        return true;
    }

    private ProductImportRowDto parseRow(Row row, int rowNumber) {
        return ProductImportRowDto.builder()
                .rowNumber(rowNumber)
                .productCode(getCellStringValue(row.getCell(0)).trim())
                .productName(getCellStringValue(row.getCell(1)).trim())
                .categoryName(getCellStringValue(row.getCell(2)).trim())
                .brand(getCellStringValue(row.getCell(3)).trim())
                .description(getCellStringValue(row.getCell(4)).trim())
                .unit(getCellStringValue(row.getCell(5)).trim())
                .costPrice(getCellStringValue(row.getCell(6)).trim())
                .sellingPrice(getCellStringValue(row.getCell(7)).trim())
                .minPrice(getCellStringValue(row.getCell(8)).trim())
                .barcode(getCellStringValue(row.getCell(9)).trim())
                .currentStock(getCellStringValue(row.getCell(10)).trim())
                .minStock(getCellStringValue(row.getCell(11)).trim())
                .location(getCellStringValue(row.getCell(12)).trim())
                .supplierName(getCellStringValue(row.getCell(13)).trim())
                .status(getCellStringValue(row.getCell(14)).trim())
                .build();
    }

    private List<String> validateRow(ProductImportRowDto row, Map<String, String> categoryMap,
                                     Map<String, String> supplierMap, Set<String> existingBarcodes,
                                     Set<String> existingCodes, Set<String> seenBarcodes, Set<String> seenCodes) {
        List<String> errors = new ArrayList<>();

        if (row.getProductName() == null || row.getProductName().isBlank()) {
            errors.add("Tên sản phẩm không được để trống");
        }
        if (row.getCategoryName() == null || row.getCategoryName().isBlank()) {
            errors.add("Danh mục không được để trống");
        } else if (!categoryMap.containsKey(row.getCategoryName().toLowerCase())) {
            errors.add("Danh mục '" + row.getCategoryName() + "' không tồn tại");
        }
        if (row.getUnit() == null || row.getUnit().isBlank()) {
            errors.add("Đơn vị không được để trống");
        }

        // Price validations
        if (row.getCostPrice() == null || row.getCostPrice().isBlank()) {
            errors.add("Giá nhập không được để trống");
        } else {
            try {
                if (new BigDecimal(row.getCostPrice()).compareTo(BigDecimal.ZERO) < 0) {
                    errors.add("Giá nhập phải >= 0");
                }
            } catch (NumberFormatException e) {
                errors.add("Giá nhập không hợp lệ");
            }
        }

        if (row.getSellingPrice() == null || row.getSellingPrice().isBlank()) {
            errors.add("Giá bán không được để trống");
        } else {
            try {
                if (new BigDecimal(row.getSellingPrice()).compareTo(BigDecimal.ZERO) < 0) {
                    errors.add("Giá bán phải >= 0");
                }
            } catch (NumberFormatException e) {
                errors.add("Giá bán không hợp lệ");
            }
        }

        // Stock validations
        if (!row.getCurrentStock().isBlank()) {
            try {
                if (Integer.parseInt(row.getCurrentStock()) < 0) errors.add("Tồn kho phải >= 0");
            } catch (NumberFormatException e) {
                errors.add("Tồn kho không hợp lệ");
            }
        }
        if (!row.getMinStock().isBlank()) {
            try {
                if (Integer.parseInt(row.getMinStock()) < 0) errors.add("Tồn kho tối thiểu phải >= 0");
            } catch (NumberFormatException e) {
                errors.add("Tồn kho tối thiểu không hợp lệ");
            }
        }

        // Status
        if (!row.getStatus().isBlank()) {
            String s = row.getStatus().toLowerCase();
            if (!s.equals("active") && !s.equals("inactive")) {
                errors.add("Trạng thái phải là Active hoặc Inactive");
            }
        }

        // Supplier
        if (!row.getSupplierName().isBlank() && !supplierMap.containsKey(row.getSupplierName().toLowerCase())) {
            errors.add("Nhà cung cấp '" + row.getSupplierName() + "' không tồn tại");
        }

        // Barcode duplicate check
        if (!row.getBarcode().isBlank()) {
            if (existingBarcodes.contains(row.getBarcode()) || seenBarcodes.contains(row.getBarcode())) {
                errors.add("Barcode '" + row.getBarcode() + "' đã tồn tại");
            } else {
                seenBarcodes.add(row.getBarcode());
            }
        }

        // Product code duplicate check
        if (!row.getProductCode().isBlank()) {
            if (existingCodes.contains(row.getProductCode()) || seenCodes.contains(row.getProductCode())) {
                errors.add("Mã sản phẩm '" + row.getProductCode() + "' đã tồn tại");
            } else {
                seenCodes.add(row.getProductCode());
            }
        }

        return errors;
    }

    private Product findExisting(ProductImportRowDto row) {
        if (!row.getProductCode().isBlank()) {
            Optional<Product> byCode = productRepository.findByCode(row.getProductCode());
            if (byCode.isPresent()) return byCode.get();
        }
        if (!row.getBarcode().isBlank()) {
            Optional<Product> byBarcode = productRepository.findByBarcode(row.getBarcode());
            if (byBarcode.isPresent()) return byBarcode.get();
        }
        return null;
    }

    private Product createProduct(ProductImportRowDto row, Map<String, String> categoryMap, Map<String, String> supplierMap) {
        Product product = new Product();
        String code = row.getProductCode().isBlank() ? generateProductCode() : row.getProductCode();
        product.setCode(code);
        product.setProductName(row.getProductName());
        product.setBarcode(row.getBarcode().isBlank() ? null : row.getBarcode());
        product.setBrand(row.getBrand().isBlank() ? null : row.getBrand());
        product.setDescription(row.getDescription().isBlank() ? null : row.getDescription());
        product.setUnit(row.getUnit());
        product.setCostPrice(new BigDecimal(row.getCostPrice()));
        product.setSellingPrice(new BigDecimal(row.getSellingPrice()));
        product.setMinPrice(row.getMinPrice().isBlank() ? null : new BigDecimal(row.getMinPrice()));
        product.setCurrentStock(row.getCurrentStock().isBlank() ? 0 : Integer.parseInt(row.getCurrentStock()));
        product.setMinStock(row.getMinStock().isBlank() ? 5 : Integer.parseInt(row.getMinStock()));
        product.setLocation(row.getLocation().isBlank() ? null : row.getLocation());
        product.setTrackInventory(true);
        product.setIsActive(row.getStatus().isBlank() || row.getStatus().equalsIgnoreCase("active"));

        String catCode = categoryMap.get(row.getCategoryName().toLowerCase());
        if (catCode != null) {
            categoryRepository.findByCode(catCode).ifPresent(product::setCategory);
        }

        if (!row.getSupplierName().isBlank()) {
            String supCode = supplierMap.get(row.getSupplierName().toLowerCase());
            if (supCode != null) {
                supplierRepository.findByCode(supCode).ifPresent(product::setDefaultSupplier);
            }
        }

        return product;
    }

    private void updateProduct(Product product, ProductImportRowDto row, Map<String, String> categoryMap, Map<String, String> supplierMap) {
        product.setProductName(row.getProductName());
        if (!row.getBarcode().isBlank()) product.setBarcode(row.getBarcode());
        if (!row.getBrand().isBlank()) product.setBrand(row.getBrand());
        if (!row.getDescription().isBlank()) product.setDescription(row.getDescription());
        product.setUnit(row.getUnit());
        product.setCostPrice(new BigDecimal(row.getCostPrice()));
        product.setSellingPrice(new BigDecimal(row.getSellingPrice()));
        if (!row.getMinPrice().isBlank()) product.setMinPrice(new BigDecimal(row.getMinPrice()));
        if (!row.getCurrentStock().isBlank()) product.setCurrentStock(Integer.parseInt(row.getCurrentStock()));
        if (!row.getMinStock().isBlank()) product.setMinStock(Integer.parseInt(row.getMinStock()));
        if (!row.getLocation().isBlank()) product.setLocation(row.getLocation());
        if (!row.getStatus().isBlank()) product.setIsActive(row.getStatus().equalsIgnoreCase("active"));

        String catCode = categoryMap.get(row.getCategoryName().toLowerCase());
        if (catCode != null) {
            categoryRepository.findByCode(catCode).ifPresent(product::setCategory);
        }

        if (!row.getSupplierName().isBlank()) {
            String supCode = supplierMap.get(row.getSupplierName().toLowerCase());
            if (supCode != null) {
                supplierRepository.findByCode(supCode).ifPresent(product::setDefaultSupplier);
            }
        }
    }

    private String generateProductCode() {
        long count = productRepository.countByCodeStartingWith("PRD");
        return String.format("PRD%05d", count + 1);
    }

    private String getCellStringValue(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> {
                double val = cell.getNumericCellValue();
                if (val == Math.floor(val) && !Double.isInfinite(val)) {
                    yield String.valueOf((long) val);
                }
                yield String.valueOf(val);
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> {
                try {
                    yield cell.getStringCellValue();
                } catch (Exception e) {
                    yield String.valueOf(cell.getNumericCellValue());
                }
            }
            default -> "";
        };
    }

    private ProductImportHistoryDto toHistoryDto(ProductImportHistory entity) {
        return ProductImportHistoryDto.builder()
                .id(entity.getId())
                .fileName(entity.getFileName())
                .importedBy(entity.getImportedBy())
                .importedAt(entity.getImportedAt())
                .totalRows(entity.getTotalRows())
                .successRows(entity.getSuccessRows())
                .failedRows(entity.getFailedRows())
                .updatedRows(entity.getUpdatedRows())
                .skippedRows(entity.getSkippedRows())
                .status(entity.getStatus())
                .importMode(entity.getImportMode())
                .errorFilePath(entity.getErrorFilePath())
                .build();
    }
}
