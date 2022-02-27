package com.todayhouse.domain.category.application;

import com.todayhouse.domain.category.domain.Category;
import com.todayhouse.domain.category.dto.request.CategorySaveRequest;
import com.todayhouse.domain.category.dto.request.CategoryUpdateRequest;
import com.todayhouse.domain.category.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {
    Category addCategory(CategorySaveRequest request);

    Category updateCategory(CategoryUpdateRequest request);

    CategoryResponse findAllByName(String name);

    List<CategoryResponse> findAll();

    void deleteCategory(String name);
}