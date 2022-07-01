package com.todayhouse.domain.story.dto.reqeust;

import com.todayhouse.domain.story.domain.FamilyType;
import com.todayhouse.domain.story.domain.ResiType;
import com.todayhouse.domain.story.domain.Story;
import com.todayhouse.domain.story.domain.StyleType;
import com.todayhouse.domain.user.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
public class StoryCreateRequest {
    @Length(min = 1, max = 50, message = "제목은 1자 이상 50자 이하로 입력해주세요.")
    private String title;
    private String content;
    @NotNull(message = "카테고리를 선택해주세요.")
    private Story.Category category;
    private Integer liked;
    private User user;
    private ResiType resiType;
    private Integer floorSpace;
    private FamilyType familyType;
    private StyleType styleType;

    @Builder
    public StoryCreateRequest(String title, String content, Story.Category category, Integer liked, User user, ResiType resiType, Integer floorSpace, FamilyType familyType, StyleType styleType) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.liked = liked;
        this.user = user;
        this.resiType = resiType;
        this.floorSpace = floorSpace;
        this.familyType = familyType;
        this.styleType = styleType;
    }

    public Story toEntity(User user) {
        return Story.builder()
                .title(title)
                .content(content)
                .familyType(familyType)
                .floorSpace(floorSpace)
                .resiType(resiType)
                .styleType(styleType)
                .liked(0)
                .category(category)
                .user(user)
                .build();
    }
}