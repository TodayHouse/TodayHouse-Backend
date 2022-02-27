package com.todayhouse.domain.category.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CategorySaveRequest {
    private String name;
    private String parentName;
}
