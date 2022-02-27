package com.todayhouse.domain.category.dto.request;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
public class CategoryUpdateRequest {
    private String name;
    private String changeName;
}
