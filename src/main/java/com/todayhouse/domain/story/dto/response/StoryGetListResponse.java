package com.todayhouse.domain.story.dto.response;

import com.todayhouse.domain.story.domain.Story;
import lombok.Getter;

@Getter
public class StoryGetListResponse {
    private final Long id;
    private final String title;
    private final String writer;

    public StoryGetListResponse(Story story){
        this.id = story.getId();
        this.title = story.getTitle();
        this.writer = story.getUser().getNickname();
    }
}
