package com.todayhouse.domain.story.dto.response;

import com.todayhouse.domain.story.domain.Story;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StoryGetDetailResponse {

    private Long id;
    private String title;
    private String content;
    private String writer;
    private Integer liked;
    private Integer views;
    private List<String> imageUrls;
    private Story.Category category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public StoryGetDetailResponse(Story story, List<String> imageUrls) {
        this.id = story.getId();
        this.title = story.getTitle();
        this.content = story.getContent();
        this.writer = story.getUser().getNickname();
        this.liked = story.getLiked();
        this.views = story.getViews();
        this.imageUrls = imageUrls;
        this.category = story.getCategory();
        this.createdAt = story.getCreatedAt();
        this.updatedAt = story.getUpdatedAt();
    }
}
