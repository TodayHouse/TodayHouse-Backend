package com.todayhouse.domain.likes.application;

import com.todayhouse.domain.likes.dao.LikesProductRepository;
import com.todayhouse.domain.likes.domain.LikesProduct;
import com.todayhouse.domain.likes.domain.LikesType;
import com.todayhouse.domain.likes.dto.LikesRequest;
import com.todayhouse.domain.likes.dto.LikesResponse;
import com.todayhouse.domain.likes.dto.UnLikesRequest;
import com.todayhouse.domain.likes.dto.UnLikesResponse;
import com.todayhouse.domain.product.dao.ProductRepository;
import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.domain.user.exception.UserNotFoundException;
import com.todayhouse.global.error.BaseException;
import com.todayhouse.global.error.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikesProductServiceImpl implements LikesService {
    private final LikesProductRepository likesProductRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    public LikesResponse likes(User principal, LikesRequest request) {
        User user = userRepository.findByEmail(principal.getEmail()).orElseThrow(UserNotFoundException::new);
        Optional<LikesProduct> likesProduct = likesProductRepository.findByUser_IdAndProduct_Id(user.getId(), request.getTypeId());
        if (likesProduct.isPresent()) {
            throw new BaseException(BaseResponseStatus.LIKES_DUPLICATE_EXCEPTION);

        }
        Product product = productRepository.findById(request.getTypeId()).orElseThrow(() -> new BaseException(BaseResponseStatus.PRODUCT_NOT_FOUND));
        LikesProduct save = likesProductRepository.save(new LikesProduct(user, product));
        product.getLikesProducts().add(save);

        return new LikesResponse(likesProductRepository.countByProduct_Id(product.getId()), true);
    }

    @Override
    public UnLikesResponse unlikes(User principal, UnLikesRequest request) {
        User user = userRepository.findByEmail(principal.getEmail()).orElseThrow(UserNotFoundException::new);

        LikesProduct likesProduct = likesProductRepository
                .findByUser_IdAndProduct_Id(user.getId(), request.getTypeId())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.PRODUCT_NOT_FOUND));

        Product product = likesProduct.getProduct();
        Long productId = product.getId();
        if (likesProduct.getUser().getId().equals(user.getId())) {
            product.getLikesProducts().remove(likesProduct);
            likesProductRepository.delete(likesProduct);
            return new UnLikesResponse(likesProductRepository.countByProduct_Id(productId), false);
        }
        throw new BaseException(BaseResponseStatus.LIKES_DELETE_EXCEPTION);
    }

    @Override
    public boolean isMatching(LikesType likesType) {
        return likesType == LikesType.PRODUCT;
    }

    public Boolean isLiked(User user, Product product) {
        if (user == null) {
            return false;
        } else {
            return likesProductRepository.existsByProduct_IdAndUser_Email(product.getId(), user.getEmail());
        }

    }

}
