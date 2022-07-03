package com.todayhouse.domain.scrap.application;

import com.todayhouse.domain.product.dao.ProductRepository;
import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.product.exception.ProductNotFoundException;
import com.todayhouse.domain.scrap.dao.ScrapRepository;
import com.todayhouse.domain.scrap.domain.Scrap;
import com.todayhouse.domain.scrap.exception.ScrapExistException;
import com.todayhouse.domain.scrap.exception.ScrapNotFoundException;
import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.domain.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ScrapServiceImpl implements ScrapService {
    private final UserRepository userRepository;
    private final ScrapRepository scrapRepository;
    private final ProductRepository productRepository;

    @Override
    public Scrap saveScrap(Long productId) {
        User user = getValidUser();
        Product product = productRepository.findById(productId).orElseThrow(ProductNotFoundException::new);
        scrapRepository.findByUserAndProduct(user, product).ifPresent(s -> {
            throw new ScrapExistException();
        });

        Scrap scrap = Scrap.builder().user(user).product(product).build();
        return scrapRepository.save(scrap);
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean isScraped(Long productId) {
        Scrap scrap = findScrapNullable(productId);
        return scrap != null;
    }

    @Override
    public void deleteScrap(Long productId) {
        Scrap scrap = findScrapNullable(productId);
        if (scrap == null)
            throw new ScrapNotFoundException();
        scrapRepository.delete(scrap);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countScrapByProductId(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(ProductNotFoundException::new);
        return scrapRepository.countByProduct(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countMyScrap() {
        User user = getValidUser();
        return scrapRepository.countByUser(user);
    }

    private User getValidUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
    }

    private Scrap findScrapNullable(Long productId) {
        User user = getValidUser();
        Product product = productRepository.findById(productId).orElseThrow(ProductNotFoundException::new);
        return scrapRepository.findByUserAndProduct(user, product).orElse(null);
    }
}
