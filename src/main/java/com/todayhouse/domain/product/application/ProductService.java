package com.todayhouse.domain.product.application;

import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.product.dto.request.ProductSaveRequest;
import com.todayhouse.domain.product.dto.request.ProductUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    Product saveProduct(ProductSaveRequest request);

    Page<Product> findAll(Pageable pageable);

    Product findOne(Long id);

    Product updateProduct(ProductUpdateRequest request);

    void removeProduct(Long id);
}
