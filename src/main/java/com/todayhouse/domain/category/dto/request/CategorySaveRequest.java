package com.todayhouse.domain.category.dto.request;

import lombok.*;

import javax.validation.constraints.NotEmpty;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CategorySaveRequest {
    @NotEmpty(message = "카테고리 이름을 입력해주세요.")
    private String name;
    private Long parentId;
}
