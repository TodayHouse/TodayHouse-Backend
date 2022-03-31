package com.todayhouse.domain.category.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.todayhouse.domain.category.domain.Category;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategoryResponse {
    private String name;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<CategoryResponse> subCategories = new ArrayList<>();

    public CategoryResponse(Category category) {
        this.name = category.getName();
    }
}
