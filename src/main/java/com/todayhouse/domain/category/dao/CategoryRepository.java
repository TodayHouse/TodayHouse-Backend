package com.todayhouse.domain.category.dao;

import com.todayhouse.domain.category.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);

    List<Category> findByDepth(int depth);

    // 해당 카테고리와 모든 하위 카테고리를 list에 추가
    @Query(value = "with recursive rec(category_id, name, depth, parent_id) as (" +
            " select category_id, name, depth, parent_id from Category where category_id=:categoryId" +
            " union all " +
            " select ch.category_id, ch.name, ch.depth, ch.parent_id from rec as par, Category as ch where par.category_id = ch.parent_id" +
            " ) " +
            " select * from rec as r" +
            " order by r.depth, r.parent_id, r.category_id",
            nativeQuery = true)
    List<Category> findOneWithAllChildrenById(@Param("categoryId") Long categoryId);

    List<Category> findAllByOrderByDepthAscParentAscIdAsc();

    boolean existsByName(String name);
}
