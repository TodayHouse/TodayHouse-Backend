package com.todayhouse.domain.category.dto.request;

import lombok.*;

import javax.validation.constraints.NotEmpty;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CategoryUpdateRequest {
    @NotEmpty(message = "변경할 카테고리 ID를 입력해주세요.")
    private Long id;
    @NotEmpty(message = "변경할 이름을 입력해주세요.")
    private String changeName;
}
