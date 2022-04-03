package com.todayhouse.domain.category.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.todayhouse.domain.category.domain.Category;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategoryResponse {
    private String name;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<CategoryResponse> subCategories = new ArrayList<>();

    public CategoryResponse(Category category) {
        this.name = category.getName();
    }

    public static CategoryResponse createCategoryResponse(List<Category> categories) {
        Category rootCategory = categories.get(0);
        Map<Long, CategoryResponse> map = new HashMap<>();
        categories.stream().forEach(c -> {
            CategoryResponse response = new CategoryResponse(c);
            map.put(c.getId(), response);
            if (c.getId() != rootCategory.getId())
                map.get(c.getParent().getId()).getSubCategories().add(response);
        });
        return map.get(rootCategory.getId());
    }

    public static List<CategoryResponse> createCategoryResponsesAll(List<Category> categories) {
        Map<Long, CategoryResponse> map = new HashMap<>();
        List<CategoryResponse> responses = new ArrayList<>();
        categories.stream().forEach(c -> {
            CategoryResponse response = new CategoryResponse(c);
            map.put(c.getId(), response);
            if (c.getDepth() != 0)
                map.get(c.getParent().getId()).getSubCategories().add(response);
            else
                responses.add(response);
        });
        return responses;
    }
}
