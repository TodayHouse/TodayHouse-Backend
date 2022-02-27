package com.todayhouse.domain.category.application;

import com.todayhouse.domain.category.dao.CategoryRepository;
import com.todayhouse.domain.category.domain.Category;
import com.todayhouse.domain.category.dto.request.CategorySaveRequest;
import com.todayhouse.domain.category.dto.request.CategoryUpdateRequest;
import com.todayhouse.domain.category.dto.response.CategoryResponse;
import com.todayhouse.domain.category.exception.CategoryExistException;
import com.todayhouse.domain.category.exception.CategoryNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public Category addCategory(CategorySaveRequest request) {
        if (categoryRepository.existsByName(request.getName()))
            throw new CategoryExistException();

        Category par = request.getParentName() == null ?
                null : categoryRepository.findByName(request.getParentName()).orElseThrow(CategoryNotFoundException::new);
        Category child = Category.builder().name(request.getName()).parent(par).build();

        return categoryRepository.save(child);
    }

    @Override
    public Category updateCategory(CategoryUpdateRequest request) {
        Category category = categoryRepository.findByName(request.getName()).orElseThrow(CategoryNotFoundException::new);
        category.updateName(request.getChangeName());
        return category;
    }

    @Override
    public void deleteCategory(String name) {
        categoryRepository.deleteByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> findAll() {
        List<Category> categories = categoryRepository.findByDepth(0);
        return categories.stream().map(c -> new CategoryResponse(c)).collect(Collectors.toList());
    }
}
