package com.todayhouse.domain.story.dto.response;

import com.todayhouse.domain.story.domain.Story;
import lombok.Getter;

@Getter
public class StoryGetListResponse {
    private final Long id;
    private final String title;
    private final String writer;
    private final String thumbnailUrl;

    public StoryGetListResponse(Story story, String thumbnailUrl){
        this.id = story.getId();
        this.title = story.getTitle();
        this.writer = story.getUser().getNickname();
        this.thumbnailUrl = thumbnailUrl;
    }
}
