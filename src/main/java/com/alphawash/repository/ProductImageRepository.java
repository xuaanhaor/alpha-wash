package com.alphawash.repository;

import com.alphawash.entity.ProductImage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    List<ProductImage> findByProduct_CodeAndDeleteFlagFalseOrderByDisplayOrderAsc(String productCode);
}
