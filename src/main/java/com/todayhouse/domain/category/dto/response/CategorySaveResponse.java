package com.todayhouse.domain.category.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.todayhouse.domain.category.domain.Category;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CategorySaveResponse {
    private Long id;
    private String name;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String parentName;

    public CategorySaveResponse(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        if (category.getParent() != null)
            this.parentName = category.getParent().getName();
    }
}
