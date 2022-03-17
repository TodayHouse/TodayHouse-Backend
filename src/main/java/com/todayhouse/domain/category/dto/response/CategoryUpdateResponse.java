package com.todayhouse.domain.category.dto.response;

import com.todayhouse.domain.category.domain.Category;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategoryUpdateResponse {
    private Long id;
    private String name;

    public CategoryUpdateResponse(Category category) {
        this.id = category.getId();
        this.name = category.getName();
    }
}
