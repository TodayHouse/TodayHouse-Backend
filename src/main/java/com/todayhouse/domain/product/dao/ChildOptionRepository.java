package com.todayhouse.domain.product.dao;

import com.todayhouse.domain.product.domain.ChildOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface ChildOptionRepository extends JpaRepository<ChildOption, Long> {
    @Query("select c from ChildOption c where c.parent.id = :parentOptionId")
    Set<ChildOption> findByParentOptionId(@Param("parentOptionId") Long parentOptionId);
}
