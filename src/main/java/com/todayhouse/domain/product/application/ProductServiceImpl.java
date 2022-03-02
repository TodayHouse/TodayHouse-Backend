package com.todayhouse.domain.product.application;

import com.todayhouse.domain.category.dao.CategoryRepository;
import com.todayhouse.domain.category.domain.Category;
import com.todayhouse.domain.category.exception.CategoryNotFoundException;
import com.todayhouse.domain.product.dao.CustomProductRepository;
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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public Product saveProductRequest(ProductSaveRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow(CategoryNotFoundException::new);
        // jwt로 seller 찾기
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        if (user.getSeller() == null)
            throw new SellerNotFoundException();
        Product product = request.toEntity(user.getSeller(), category);
        return productRepository.save(product);
    }

    @Override
    public Page<ProductResponse> findAll(ProductSearchRequest productSearch, Pageable pageable) {
        Page<ProductResponse> response = productRepository.findAll(productSearch, pageable)
                .map(p -> new ProductResponse(p));
        return response;
    }

    @Override
    public Product findOne(Long id) {
        return productRepository.findById(id).orElseThrow(ProductNotFoundException::new);
    }

    @Override
    public Product updateProduct(ProductUpdateRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow(CategoryNotFoundException::new);
        Product product = getValidProduct(request.getId());
        product.updateProduct(request, category);
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
        Product product = productRepository.findById(id).orElseThrow(ProductNotFoundException::new);
        if (!user.getSeller().equals(product.getSeller()))
            throw new InvalidRequestException();
        return product;
    }
}
