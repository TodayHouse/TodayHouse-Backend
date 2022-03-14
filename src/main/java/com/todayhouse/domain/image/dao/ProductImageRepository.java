package com.todayhouse.domain.image.dao;

import com.todayhouse.domain.image.domain.ProductImage;
import com.todayhouse.domain.product.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    Optional<ProductImage> findFirstByProductOrderByCreatedAtAsc(Product product);

    Optional<ProductImage> findByFileName(String fileName);

    List<ProductImage> findByProductId(Long productId);

    Long countByProductId(Long productId);

    void deleteByFileName(String fileName);
}
