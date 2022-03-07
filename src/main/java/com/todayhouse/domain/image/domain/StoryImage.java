package com.todayhouse.domain.image.domain;

import com.todayhouse.domain.story.domain.Story;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("image")
public class StoryImage extends Image {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id", nullable = false)
    private Story story;

    @Builder
    public StoryImage(String fileName, Story story) {
        super(fileName);
        this.story = story;
    }

    public static class StoryImageBuilder extends ImageBuilder {
        StoryImageBuilder() {
            super();
        }
    }
}
