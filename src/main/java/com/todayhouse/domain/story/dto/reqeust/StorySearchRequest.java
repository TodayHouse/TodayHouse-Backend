package com.todayhouse.domain.story.dto.reqeust;

import com.todayhouse.domain.story.domain.FamilyType;
import com.todayhouse.domain.story.domain.ResiType;
import com.todayhouse.domain.story.domain.Story;
import com.todayhouse.domain.story.domain.StyleType;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class StorySearchRequest {
    private String search;
    private ResiType resiType;
    private StyleType styleType;
    private FamilyType familyType;
    private Integer floorSpaceMin;
    private Integer floorSpaceMax;
    private Story.Category category;
}
