package com.todayhouse.domain.category.dao;

import com.todayhouse.domain.category.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);

    List<Category> findByDepth(int depth);

    boolean existsByName(String name);

    void deleteByName(String name);
}
