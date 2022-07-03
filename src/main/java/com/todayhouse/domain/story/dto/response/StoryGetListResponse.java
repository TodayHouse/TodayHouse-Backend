package com.todayhouse.domain.story.dto.response;

import com.todayhouse.domain.story.domain.Story;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StoryGetListResponse {
    private Long id;
    private String title;
    private String writer;
    private String thumbnailUrl;

    public StoryGetListResponse(Story story, String thumbnailUrl) {
        this.id = story.getId();
        this.title = story.getTitle();
        this.writer = story.getUser().getNickname();
        this.thumbnailUrl = thumbnailUrl;
    }
}
