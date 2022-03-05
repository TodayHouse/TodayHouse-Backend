package com.todayhouse.domain.product.dao;

import com.todayhouse.domain.product.domain.ParentOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface ParentOptionRepository extends JpaRepository<ParentOption, Long> {
    @Query("select p from ParentOption p where p.product.id = :productId")
    Set<ParentOption> findByProductId(@Param("productId") Long productId);
}
