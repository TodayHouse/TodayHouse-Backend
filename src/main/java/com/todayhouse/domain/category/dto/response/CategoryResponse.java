package com.todayhouse.domain.category.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.todayhouse.domain.category.domain.Category;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CategoryResponse {
    private Long id;
    private String name;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<CategoryResponse> subCategory;

    public CategoryResponse(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        if (!category.getChildren().isEmpty()) {
            this.subCategory = category.getChildren().stream()
                    .map(c -> new CategoryResponse(c)).collect(Collectors.toList());
        }
    }
}
