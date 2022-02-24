package com.todayhouse.domain.product.application;

import com.todayhouse.domain.product.dao.CustomProductRepository;
import com.todayhouse.domain.product.dao.ProductRepository;
import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.product.dto.request.ProductSaveRequest;
import com.todayhouse.domain.product.dto.request.ProductUpdateRequest;
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
    private final CustomProductRepository customProductRepository;

    @Override
    public Product saveProduct(ProductSaveRequest request) {
        // jwt로 seller 찾기
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        if (user.getSeller() == null)
            throw new SellerNotFoundException();
        Product product = request.toEntity(user.getSeller());
        return productRepository.save(product);
    }

    @Override
    public Page<Product> findAll(Pageable pageable) {
        return customProductRepository.findAll(pageable);
    }

    @Override
    public Product findOne(Long id) {
        return productRepository.findById(id).orElseThrow(ProductNotFoundException::new);
    }

    @Override
    public Product updateProduct(ProductUpdateRequest request) {
        // jwt의 email과 product를 등록한 user의 email이 같은지 확인
        Product product = getValidProduct(request.getId());
        product.updateProduct(request);
        return productRepository.save(product);
    }

    @Override
    public void removeProduct(Long id) {
        getValidProduct(id);
        productRepository.deleteById(id);
    }

    // product의 user email과 jwt의 email이 같은지 확인
    private Product getValidProduct(Long id){
        String jwtEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Product product = productRepository.findById(id).orElseThrow(ProductNotFoundException::new);
        String email = product.getSeller().getUser().getEmail();
        if (!email.equals(jwtEmail))
            throw new InvalidRequestException();
        return product;
    }
}
