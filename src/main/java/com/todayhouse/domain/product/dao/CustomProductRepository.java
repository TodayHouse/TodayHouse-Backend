package com.todayhouse.domain.product.dao;

import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.product.dto.request.ProductSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CustomProductRepository {
    Page<Product> findAllWithSeller(ProductSearchRequest productSearch, Pageable pageable);

    Optional<Product> findByIdWithOptionsAndSellerAndImages(Long id);
}
