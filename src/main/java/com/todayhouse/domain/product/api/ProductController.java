package com.todayhouse.domain.product.api;

import com.todayhouse.domain.image.application.ImageService;
import com.todayhouse.domain.product.application.ProductService;
import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.product.dto.request.ProductSaveRequest;
import com.todayhouse.domain.product.dto.request.ProductSearchRequest;
import com.todayhouse.domain.product.dto.request.ProductUpdateRequest;
import com.todayhouse.domain.product.dto.request.SaveProductImagesRequest;
import com.todayhouse.domain.product.dto.response.ProductResponse;
import com.todayhouse.domain.product.dto.response.ProductSearchResponse;
import com.todayhouse.global.common.BaseResponse;
import com.todayhouse.infra.S3Storage.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final FileService fileService;
    private final ImageService imageService;
    private final ProductService productService;

    @PostMapping
    public BaseResponse<Long> saveProduct(@RequestPart(value = "file", required = false) List<MultipartFile> multipartFiles,
                                          @RequestPart(value = "request") @Valid ProductSaveRequest request) {
        Long productId = productService.saveProductRequest(multipartFiles, request);
        return new BaseResponse(Collections.singletonMap("productId", productId));
    }

    @PostMapping("/images")
    public BaseResponse<Long> saveImages(@RequestPart(value = "file", required = false) List<MultipartFile> multipartFiles,
                                         @RequestPart(value = "request") @Valid SaveProductImagesRequest request) {
        Long productId = productService.saveProductImages(multipartFiles, request.getProductId());
        return new BaseResponse(Collections.singletonMap("productId", productId));
    }

    //?page=0&size=4&sort=price,DESC&sort=id,DESC 형식으로 작성
    //ProductSearchRequest은 선택사항
    @GetMapping
    public BaseResponse findProductsPagination(@RequestBody(required = false) ProductSearchRequest productSearch,
                                               Pageable pageable) {
        Page<ProductResponse> products = productService.findAllWithSeller(productSearch, pageable);
        return new BaseResponse(new ProductSearchResponse(products));
    }

    @GetMapping("/{id}")
    public BaseResponse findProductWithImages(@PathVariable Long id) {
        Product product = productService.findByIdWithOptionsAndSellerAndImages(id);
        List<String> fileNames = imageService.findProductImageFileNamesByProductId(id);
        List<String> imageUrls = fileNames.stream().filter(Objects::nonNull)
                .map(i -> fileService.changeFileNameToUrl(i))
                .collect(Collectors.toList());
        ProductResponse response = new ProductResponse(product);
        response.setImages(imageUrls);
        return new BaseResponse(response);
    }

    @PutMapping
    public BaseResponse updateProduct(@RequestBody ProductUpdateRequest request) {
        Product product = productService.updateProduct(request);
        return new BaseResponse(new ProductResponse(product));
    }

    @DeleteMapping("/{id}")
    public BaseResponse deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return new BaseResponse("삭제되었습니다.");
    }

    @DeleteMapping("/images/{file}")
    public BaseResponse deleteProductImage(@PathVariable String file) {
        imageService.deleteProductImages(List.of(file));
        return new BaseResponse("삭제되었습니다.");
    }
}
