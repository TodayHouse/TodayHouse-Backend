package com.todayhouse.domain.likes.domain;

import com.todayhouse.domain.story.domain.Story;
import com.todayhouse.domain.user.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@DiscriminatorColumn
@Inheritance(strategy = InheritanceType.JOINED)
@Entity
@NoArgsConstructor
@Getter
public class LikesStory extends Likes {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id")
    private Story story;

    public LikesStory(User user, Story story) {
        super(user);
        this.story = story;
    }
}
