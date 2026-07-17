package com.alphawash.service.impl;

import com.alphawash.dto.QuickServiceGroupDto;
import com.alphawash.dto.QuickServiceGroupDto.QuickCatalogDto;
import com.alphawash.dto.QuickServiceGroupDto.QuickServiceDto;
import com.alphawash.dto.RecentVehicleDto;
import com.alphawash.service.QuickInvoiceService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuickInvoiceServiceImpl implements QuickInvoiceService {

    private final EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public List<QuickServiceGroupDto> getGroupedServices() {
        String sql = """
            SELECT st.code              AS service_type_code,
                   st.service_type_name  AS service_type_name,
                   s.code               AS service_code,
                   s.service_name       AS service_name,
                   s.duration           AS duration,
                   sc.code              AS catalog_code,
                   sc.size::text        AS size,
                   sc.price             AS price
            FROM service_type st
            JOIN service s ON s.service_type_code = st.code
            JOIN service_catalog sc ON sc.service_code = s.code
            WHERE st.delete_flag = false
              AND s.delete_flag = false
              AND sc.delete_flag = false
            ORDER BY st.code, s.code, sc.size
            """;

        Query query = entityManager.createNativeQuery(sql);

        @SuppressWarnings("unchecked")
        List<Object[]> rows = query.getResultList();

        // Group: serviceTypeCode -> serviceCode -> flat rows
        Map<String, Map<String, List<Object[]>>> grouped = new LinkedHashMap<>();

        for (Object[] row : rows) {
            String typeCode = (String) row[0];
            String serviceCode = (String) row[2];

            grouped.computeIfAbsent(typeCode, k -> new LinkedHashMap<>())
                    .computeIfAbsent(serviceCode, k -> new ArrayList<>())
                    .add(row);
        }

        List<QuickServiceGroupDto> result = new ArrayList<>();

        for (var typeEntry : grouped.entrySet()) {
            Object[] firstRow = typeEntry.getValue().values().iterator().next().get(0);
            String typeName = (String) firstRow[1];

            List<QuickServiceDto> serviceDtos = new ArrayList<>();

            for (var serviceEntry : typeEntry.getValue().entrySet()) {
                List<Object[]> catalogRows = serviceEntry.getValue();
                Object[] first = catalogRows.get(0);

                List<QuickCatalogDto> catalogs = catalogRows.stream()
                        .map(r -> QuickCatalogDto.builder()
                                .catalogCode((String) r[5])
                                .size((String) r[6])
                                .price(r[7] instanceof BigDecimal bd ? bd : BigDecimal.valueOf(((Number) r[7]).doubleValue()))
                                .build())
                        .toList();

                serviceDtos.add(QuickServiceDto.builder()
                        .serviceCode((String) first[2])
                        .serviceName((String) first[3])
                        .duration((String) first[4])
                        .catalogs(catalogs)
                        .build());
            }

            result.add(QuickServiceGroupDto.builder()
                    .serviceTypeCode(typeEntry.getKey())
                    .serviceTypeName(typeName)
                    .services(serviceDtos)
                    .build());
        }

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecentVehicleDto> getRecentVehicles(int limit) {
        String sql = """
            SELECT * FROM (
                SELECT DISTINCT ON (v.id)
                    v.id::text          AS vehicle_id,
                    v.license_plate     AS license_plate,
                    v.image_url         AS image_url,
                    c.id::text          AS customer_id,
                    c.customer_name     AS customer_name,
                    c.phone             AS customer_phone,
                    b.code              AS brand_code,
                    b.brand_name        AS brand_name,
                    m.code              AS model_code,
                    m.model_name        AS model_name,
                    m.size::text        AS vehicle_size,
                    o.created_at        AS last_order_date
                FROM order_detail od
                JOIN "orders" o ON od.order_code = o.code
                JOIN vehicle v ON od.vehicle_id = v.id
                LEFT JOIN customer c ON v.customer_id = c.id
                LEFT JOIN brands b ON v.brand_code = b.code
                LEFT JOIN model m ON v.model_code = m.code
                WHERE o.delete_flag = false
                  AND v.delete_flag = false
                ORDER BY v.id, o.created_at DESC
            ) sub
            ORDER BY sub.last_order_date DESC
            LIMIT :limit
            """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("limit", limit);

        @SuppressWarnings("unchecked")
        List<Object[]> rows = query.getResultList();

        return rows.stream()
                .map(row -> RecentVehicleDto.builder()
                        .vehicleId((String) row[0])
                        .licensePlate((String) row[1])
                        .imageUrl((String) row[2])
                        .customerId((String) row[3])
                        .customerName((String) row[4])
                        .customerPhone((String) row[5])
                        .brandCode((String) row[6])
                        .brandName((String) row[7])
                        .modelCode((String) row[8])
                        .modelName((String) row[9])
                        .vehicleSize((String) row[10])
                        .lastOrderDate(row[11] instanceof Timestamp ts
                                ? ts.toLocalDateTime()
                                : (LocalDateTime) row[11])
                        .build())
                .toList();
    }
}
