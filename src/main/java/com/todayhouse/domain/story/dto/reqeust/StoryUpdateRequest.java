package com.todayhouse.domain.story.dto.reqeust;

import com.todayhouse.domain.story.domain.Story;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Getter
@RequiredArgsConstructor
public class StoryUpdateRequest {
    @Length(min = 1, max = 50, message = "제목은 1자 이상 50자 이하로 입력해주세요.")
    private String title;
    private String content;
    @NotNull(message = "카테고리를 선택해주세요.")
    private Story.Category category;
}
