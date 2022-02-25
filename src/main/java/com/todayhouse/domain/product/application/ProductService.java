package com.todayhouse.domain.product.application;

import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.product.dto.request.ProductSaveRequest;
import com.todayhouse.domain.product.dto.request.ProductSearchRequest;
import com.todayhouse.domain.product.dto.request.ProductUpdateRequest;
import com.todayhouse.domain.product.dto.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    Product saveProductRequest(ProductSaveRequest request);

    Page<ProductResponse> findAll(ProductSearchRequest productSearch, Pageable pageable);

    Product findOne(Long id);

    Product updateProduct(ProductUpdateRequest request);

    void deleteProduct(Long id);
}
