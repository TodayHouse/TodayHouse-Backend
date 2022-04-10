package com.todayhouse.domain.category.application;

import com.todayhouse.domain.category.domain.Category;
import com.todayhouse.domain.category.dto.request.CategorySaveRequest;
import com.todayhouse.domain.category.dto.request.CategoryUpdateRequest;

import java.util.List;

public interface CategoryService {
    Category addCategory(CategorySaveRequest request);

    Category updateCategory(CategoryUpdateRequest request);

    List<Category> findRootPath(String categoryName);

    List<Category> findAllWithChildrenAll();

    List<Category> findOneByNameWithChildrenAll(String categoryName);

    void deleteCategory(String categoryName);
}
