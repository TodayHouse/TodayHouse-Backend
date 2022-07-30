package com.todayhouse.domain.story.dto.response;

import com.todayhouse.domain.story.domain.*;
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
    private Integer views;
    private List<String> imageUrls;
    private Story.Category category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Integer floorSpace;
    private ResiType resiType;
    private FamilyType familyType;
    private StyleType styleType;
    private Writer writer;
    private Integer likesCount;
    private boolean liked;


    public StoryGetDetailResponse(Story story, List<String> imageUrls) {
        this.id = story.getId();
        this.title = story.getTitle();
        this.content = story.getContent();
        this.views = story.getViews();
        this.imageUrls = imageUrls;
        this.category = story.getCategory();
        this.createdAt = story.getCreatedAt();
        this.updatedAt = story.getUpdatedAt();
        this.floorSpace = story.getFloorSpace();
        this.resiType = story.getResiType();
        this.familyType = story.getFamilyType();
        this.styleType = story.getStyleType();
        this.writer = new Writer(story.getUser().getId(), story.getUser().getNickname(), story.getUser().getProfileImage());

    }

    @Getter
    @NoArgsConstructor
    private static class Writer {
        private Long id;
        private String nickname;
        private String profileImage;

        public Writer(Long id, String nickname, String profileImage) {
            this.id = id;
            this.nickname = nickname;
            this.profileImage = profileImage;
        }
    }

    public void liked(boolean liked) {
        this.liked = liked;
    }
}
