package com.todayhouse.domain.likes.domain;

import com.todayhouse.domain.story.domain.StoryReply;
import com.todayhouse.domain.user.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@DiscriminatorColumn
@Inheritance(strategy = InheritanceType.JOINED)
@Entity
@NoArgsConstructor
@Getter
public class LikesStoryReply extends Likes{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_reply_id")
    private StoryReply storyReply;


    public LikesStoryReply(User user, StoryReply storyReply) {
        super(user);
        this.storyReply = storyReply;
    }
}
