package com.todayhouse.domain.product.dao;

import com.todayhouse.domain.product.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomProductRepository {
    Page<Product> findAll(Pageable pageable);
}
