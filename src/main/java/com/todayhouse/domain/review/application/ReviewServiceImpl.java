package com.todayhouse.domain.review.application;

import com.todayhouse.domain.product.dao.ProductRepository;
import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.product.exception.ProductNotFoundException;
import com.todayhouse.domain.review.dao.ReviewRepository;
import com.todayhouse.domain.review.domain.Review;
import com.todayhouse.domain.review.dto.request.ReviewSaveRequest;
import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.domain.user.exception.UserNotFoundException;
import com.todayhouse.infra.S3Storage.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final FileService fileService;

    @Override
    public Long saveReview(MultipartFile multipartFile, ReviewSaveRequest request) {
        User user = getValidUser();
        String imageUrl = saveFileAndGetUrl(multipartFile);
        Product product = getValidProduct(request.getProductId());
        Review save = reviewRepository.save(request.toEntity(imageUrl, user, product));
        return save.getId();
    }

    private User getValidUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
    }

    private String saveFileAndGetUrl(MultipartFile multipartFile) {
        if (multipartFile == null) return null;
        String fileName = fileService.uploadImage(multipartFile);
        return fileService.changeFileNameToUrl(fileName);
    }

    private Product getValidProduct(Long productId) {
        return productRepository.findById(productId).orElseThrow(ProductNotFoundException::new);
    }


}
