package com.todayhouse.domain.product.application;

import com.todayhouse.domain.category.dao.CategoryRepository;
import com.todayhouse.domain.category.domain.Category;
import com.todayhouse.domain.category.exception.CategoryNotFoundException;
import com.todayhouse.domain.image.application.ImageService;
import com.todayhouse.domain.image.dao.ProductImageRepository;
import com.todayhouse.domain.product.dao.ProductRepository;
import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.product.dto.request.ProductSaveRequest;
import com.todayhouse.domain.product.dto.request.ProductSearchRequest;
import com.todayhouse.domain.product.dto.request.ProductUpdateRequest;
import com.todayhouse.domain.product.dto.response.ProductResponse;
import com.todayhouse.domain.product.exception.ProductNotFoundException;
import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.domain.user.exception.InvalidRequestException;
import com.todayhouse.domain.user.exception.SellerNotFoundException;
import com.todayhouse.domain.user.exception.UserNotFoundException;
import com.todayhouse.infra.S3Storage.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final FileService fileService;
    private final ImageService imageService;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;

    @Override
    public Long saveProductRequest(List<MultipartFile> multipartFiles, ProductSaveRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow(CategoryNotFoundException::new);
        // jwt로 seller 찾기
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        if (user.getSeller() == null)
            throw new SellerNotFoundException();
        return saveEntity(multipartFiles, request, user, category);
    }

    // seller와 join한 모든 product
    @Override
    public Page<ProductResponse> findAllWithSeller(ProductSearchRequest productSearch, Pageable pageable) {
        Page<ProductResponse> page = productRepository.findAllWithSeller(productSearch, pageable)
                .map(p -> {
                    ProductResponse response = new ProductResponse(p);
                    response.setImages(List.of(fileService.getImage(p.getImage())));
                    return response;
                });
        return page;
    }

    // product 와 image left join
    @Override
    public Product findByIdWithOptionsAndSellerAndImages(Long id) {
        return productRepository.findByIdWithOptionsAndSellerAndImages(id).orElseThrow(ProductNotFoundException::new);
    }

    @Override
    public Product updateProduct(ProductUpdateRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow(CategoryNotFoundException::new);
        Product product = getValidProduct(request.getId());
        product.update(request, category);
        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(Long id) {
        getValidProduct(id);
        productRepository.deleteById(id);
    }

    // product의 seller와 user의 seller가 같은지 확인
    private Product getValidProduct(Long id) {
        String jwtEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(jwtEmail).orElseThrow(UserNotFoundException::new);
        Product product = productRepository.findByIdWithSeller(id).orElseThrow(ProductNotFoundException::new);
        if (!user.getSeller().equals(product.getSeller()))
            throw new InvalidRequestException();
        return product;
    }

    // product, image, filename 저장
    private Long saveEntity(List<MultipartFile> multipartFiles, ProductSaveRequest request,
                            User user, Category category) {
        List<String> fileNames = new ArrayList<>();
        String first = null;
        if (multipartFiles != null && !multipartFiles.isEmpty()) {
            System.out.println(multipartFiles.size());
            fileNames = fileService.upload(multipartFiles);
            first = fileNames.get(0);
        }
        Product product = productRepository.save(request.toEntityWithParentAndSelection(user.getSeller(), category, first));
        imageService.save(fileNames, product);
        return product.getId();
    }
}
