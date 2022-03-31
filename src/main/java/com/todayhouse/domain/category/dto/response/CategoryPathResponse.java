package com.todayhouse.domain.category.dto.response;

import com.todayhouse.domain.category.domain.Category;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategoryPathResponse {
    List<String> categoryPath = new ArrayList<>();

    public CategoryPathResponse(List<Category> categoryPath) {
        categoryPath.stream().forEach(c -> this.categoryPath.add(c.getName()));
    }
}
