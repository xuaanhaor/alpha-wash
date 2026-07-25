package com.alphawash.configuration;

import com.alphawash.entity.ServiceCategoryEntity;
import com.alphawash.repository.ServiceCategoryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Seed 7 danh mục dịch vụ mặc định từ enum ServiceCategory cũ.
 * Chỉ chạy khi bảng service_category trống.
 */
@Component
@Order(10)
@RequiredArgsConstructor
@Slf4j
public class ServiceCategorySeeder implements CommandLineRunner {

    private final ServiceCategoryRepository repository;

    @Override
    public void run(String... args) {
        if (repository.countByDeleteFlagFalse() > 0) {
            log.info("ServiceCategorySeeder: bảng service_category đã có dữ liệu, bỏ qua seed.");
            return;
        }
        log.info("ServiceCategorySeeder: đang seed danh mục dịch vụ...");
        repository.saveAll(defaultCategories());
        log.info("ServiceCategorySeeder: hoàn thành seed 7 danh mục.");
    }

    private List<ServiceCategoryEntity> defaultCategories() {
        return List.of(
                ServiceCategoryEntity.builder()
                        .code("WASHING").name("Rửa xe")
                        .description("Các gói rửa xe và vệ sinh ngoại thất")
                        .sortOrder(1).active(true).build(),

                ServiceCategoryEntity.builder()
                        .code("INTERIOR").name("Nội thất")
                        .description("Vệ sinh và chăm sóc nội thất xe")
                        .sortOrder(2).active(true).build(),

                ServiceCategoryEntity.builder()
                        .code("POLISHING").name("Đánh bóng")
                        .description("Hiệu chỉnh sơn, đánh bóng, phục hồi ngoại thất")
                        .sortOrder(3).active(true).build(),

                ServiceCategoryEntity.builder()
                        .code("GLASS").name("Kính xe")
                        .description("Chăm sóc và phủ ceramic kính")
                        .sortOrder(4).active(true).build(),

                ServiceCategoryEntity.builder()
                        .code("PPF").name("Dán PPF")
                        .description("Dán PPF bảo vệ sơn và dán phim cách nhiệt")
                        .sortOrder(5).active(true).build(),

                ServiceCategoryEntity.builder()
                        .code("COMBO").name("Combo")
                        .description("Gói combo kết hợp nhiều dịch vụ")
                        .sortOrder(6).active(true).build(),

                ServiceCategoryEntity.builder()
                        .code("OTHER").name("Khác")
                        .description("Các dịch vụ và sản phẩm khác")
                        .sortOrder(7).active(true).build()
        );
    }
}
