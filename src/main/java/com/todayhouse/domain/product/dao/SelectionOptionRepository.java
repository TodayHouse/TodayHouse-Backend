package com.todayhouse.domain.product.dao;

import com.todayhouse.domain.product.domain.SelectionOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface SelectionOptionRepository extends JpaRepository<SelectionOption, Long> {
    @Query("select s from SelectionOption s where s.product.id = :productId")
    Set<SelectionOption> findByProductId(@Param("productId") Long productId);
}
