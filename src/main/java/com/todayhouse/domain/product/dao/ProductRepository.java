package com.todayhouse.domain.product.dao;

import com.todayhouse.domain.product.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("select distinct p from Product p left join fetch ProductImage pi on p.id = :id and pi.product.id = :id")
    Optional<Product> findByIdWithImages(@Param("id") Long id);
}
