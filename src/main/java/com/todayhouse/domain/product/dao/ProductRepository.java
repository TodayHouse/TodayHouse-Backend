package com.todayhouse.domain.product.dao;

import com.todayhouse.domain.product.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, CustomProductRepository {
    @Query("select p from Product p join fetch p.seller where p.id = :id")
    Optional<Product> findByIdWithSeller(@Param("id") Long id);
}
