package com.todayhouse.domain.story.dto.response;

import com.todayhouse.domain.story.domain.Story;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class StoryGetListResponse {
    private Long id;
    private String title;
    private String writer;
    private String thumbnailUrl;
    private Boolean isScraped;

    public StoryGetListResponse(Story story, String thumbnailUrl, Boolean isScraped) {
        this.id = story.getId();
        this.title = story.getTitle();
        this.writer = story.getUser().getNickname();
        this.thumbnailUrl = thumbnailUrl;
        this.isScraped = isScraped;
    }
}
