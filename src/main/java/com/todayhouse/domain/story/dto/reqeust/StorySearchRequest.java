package com.todayhouse.domain.story.dto.reqeust;

import com.todayhouse.domain.story.domain.FamilyType;
import com.todayhouse.domain.story.domain.ResiType;
import com.todayhouse.domain.story.domain.Story;
import com.todayhouse.domain.story.domain.StyleType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StorySearchRequest {
    private FamilyType familyType;
    private StyleType styleType;
    private ResiType resiType;
    private Integer floorSpaceMin;
    private Integer floorSpaceMax;
    private Story.Category category;
}
