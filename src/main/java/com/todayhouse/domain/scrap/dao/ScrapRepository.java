package com.todayhouse.domain.scrap.dao;

import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.scrap.domain.Scrap;
import com.todayhouse.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScrapRepository extends JpaRepository<Scrap, Long> {
    Optional<Scrap> findByUserAndProduct(User user, Product product);

    Long countByProduct(Product product);

    Long countByUser(User user);
}
