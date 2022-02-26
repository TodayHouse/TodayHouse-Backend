package com.todayhouse.domain.product.api;

import com.todayhouse.domain.product.application.ProductService;
import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.product.dto.request.ProductSaveRequest;
import com.todayhouse.domain.product.dto.request.ProductSearchRequest;
import com.todayhouse.domain.product.dto.request.ProductUpdateRequest;
import com.todayhouse.domain.product.dto.response.ProductResponse;
import com.todayhouse.domain.product.dto.response.ProductSaveResponse;
import com.todayhouse.domain.product.dto.response.ProductSearchResponse;
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

    //?page=0&size=4&sort=price,DESC&sort=id,DESC 형식으로 작성
    @GetMapping
    public BaseResponse findProductsPagination(@RequestBody(required = false) ProductSearchRequest productSearch,
                                               Pageable pageable) {
        Page<ProductResponse> products = productService.findAll(productSearch, pageable);
        return new BaseResponse(new ProductSearchResponse(products));
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
