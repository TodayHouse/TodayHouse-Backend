package com.todayhouse.domain.product.application;

import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.product.dto.request.ProductSaveRequest;
import com.todayhouse.domain.product.dto.request.ProductSearchRequest;
import com.todayhouse.domain.product.dto.request.ProductUpdateRequest;
import com.todayhouse.domain.product.dto.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    Long saveProductRequest(List<MultipartFile> multipartFile, ProductSaveRequest request);

    Long saveProductImages(List<MultipartFile> multipartFiles, Long productId);

    Page<ProductResponse> findAllWithSeller(ProductSearchRequest productSearch, Pageable pageable);

    Product findByIdWithOptionsAndSeller(Long id);

    Product updateProduct(ProductUpdateRequest request);

    void deleteProduct(Long id);
}
