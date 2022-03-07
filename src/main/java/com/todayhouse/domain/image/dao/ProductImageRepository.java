package com.todayhouse.domain.image.dao;

import com.todayhouse.domain.image.domain.ProductImage;
import com.todayhouse.domain.product.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    Optional<ProductImage> findFirstByProductOrderByCreatedAtDesc(Product product);
}
