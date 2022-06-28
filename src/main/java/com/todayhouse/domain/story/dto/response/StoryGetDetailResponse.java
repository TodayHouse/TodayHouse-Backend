package com.todayhouse.domain.story.dto.response;

import com.todayhouse.domain.story.domain.Story;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class StoryGetDetailResponse {

    private final Long id;
    private final String title;
    private final String content;
    private final String writer;
    private final Integer liked;
    private final List<String> imageUrls;
    private final Story.Category category;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public StoryGetDetailResponse(Story story, List<String> imageUrls) {
        this.id = story.getId();
        this.title = story.getTitle();
        this.content = story.getContent();
        this.writer = story.getUser().getNickname();
        this.liked = story.getLiked();
        this.imageUrls = imageUrls;
        this.category = story.getCategory();
        this.createdAt = story.getCreatedAt();
        this.updatedAt = story.getUpdatedAt();
    }
}
