package com.todayhouse.domain.category.api;

import com.todayhouse.domain.category.application.CategoryService;
import com.todayhouse.domain.category.domain.Category;
import com.todayhouse.domain.category.dto.request.CategorySaveRequest;
import com.todayhouse.domain.category.dto.request.CategoryUpdateRequest;
import com.todayhouse.domain.category.dto.response.CategorySaveResponse;
import com.todayhouse.domain.category.dto.response.CategoryUpdateResponse;
import com.todayhouse.global.common.BaseResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/categories")
@AllArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public BaseResponse SaveCategory(@RequestBody CategorySaveRequest request) {
        Category category = categoryService.addCategory(request);
        return new BaseResponse(new CategorySaveResponse(category));
    }

    @GetMapping
    public BaseResponse findAll() {
        return new BaseResponse(categoryService.findAll());
    }

    // 해당 카테고리의 모든 하위 카테고리
    @GetMapping("/{name}")
    public BaseResponse findSubAll(@PathVariable String name) {
        return new BaseResponse(categoryService.findAllByName(name));
    }

    @PatchMapping
    public BaseResponse updateCategoryName(@RequestBody CategoryUpdateRequest request) {
        Category category = categoryService.updateCategory(request);
        return new BaseResponse(new CategoryUpdateResponse(category));
    }

    @DeleteMapping("/{name}")
    public BaseResponse deleteCategory(@PathVariable String name) {
        categoryService.deleteCategory(name);
        return new BaseResponse();
    }
}
