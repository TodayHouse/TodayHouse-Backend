package com.todayhouse.domain.product.api;

import com.todayhouse.domain.product.application.ProductService;
import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.product.dto.request.ProductSaveRequest;
import com.todayhouse.domain.product.dto.request.ProductUpdateRequest;
import com.todayhouse.domain.product.dto.response.ProductResponse;
import com.todayhouse.domain.product.dto.response.ProductSaveResponse;
import com.todayhouse.global.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public BaseResponse saveProduct(@Valid @RequestBody ProductSaveRequest request) {
        Product product = productService.saveProductRequest(request);
        return new BaseResponse(new ProductSaveResponse(product));
    }

    @GetMapping
    public BaseResponse findProductsPagination(Pageable pageable) {
        Page<ProductResponse> products = productService.findAll(pageable);
        return new BaseResponse(products);
    }

    @GetMapping("/{id}")
    public BaseResponse findProduct(@PathVariable Long id) {
        Product product = productService.findOne(id);
        return new BaseResponse(new ProductResponse(product));
    }

    @PutMapping
    public BaseResponse updateProduct(@RequestBody ProductUpdateRequest request) {
        Product product = productService.updateProduct(request);
        return new BaseResponse(new ProductResponse(product));
    }

    @DeleteMapping("{id}")
    public BaseResponse deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return new BaseResponse(Collections.singletonMap("id", id));
    }
}
