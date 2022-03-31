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
            " select category_id, name, depth, parent_id from Category where name=:categoryName" +
            " union all " +
            " select ch.category_id, ch.name, ch.depth, ch.parent_id from rec as par, Category as ch where par.category_id = ch.parent_id" +
            " ) " +
            " select * from rec as r" +
            " order by r.depth, r.parent_id, r.category_id",
            nativeQuery = true)
    List<Category> findOneWithAllChildrenByName(@Param("categoryName") String categoryName);

    List<Category> findAllByOrderByDepthAscParentAscIdAsc();

    @Query(value = "with recursive rec(category_id, name, depth, parent_id) as (" +
            " select category_id, name, depth, parent_id from Category where name=:categoryName" +
            " union all " +
            " select par.category_id, par.name, par.depth, par.parent_id from rec as ch, Category as par where par.category_id = ch.parent_id" +
            " ) " +
            " select * from rec as r" +
            " order by r.depth",
            nativeQuery = true)
    List<Category> findRootPathByName(@Param("categoryName") String categoryName);

    boolean existsByName(String name);

    void deleteByName(String name);
}
