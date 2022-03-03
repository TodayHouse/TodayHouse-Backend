package com.todayhouse.domain.product.dao;

import com.todayhouse.domain.product.domain.SelectionOption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SelectionOptionRepository extends JpaRepository<SelectionOption, Long> {
}
