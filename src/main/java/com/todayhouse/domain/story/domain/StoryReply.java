package com.todayhouse.domain.story.domain;

import com.todayhouse.domain.likes.domain.LikesStoryReply;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.global.common.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StoryReply extends BaseTimeEntity {
    @Id
    @Column(name = "story_reply_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String content;

    @Formula(
            value = "(select count(1) from likes l where l.story_reply_id = story_reply_id)"
    )
    @Basic(fetch = FetchType.LAZY)
    private int likesCount;

    @Builder
    public StoryReply(String content, Story story, User user) {

        this.content = content;
        this.story = story;
        this.user = user;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id")
    private Story story;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "storyReply", cascade = CascadeType.ALL)
    private Set<LikesStoryReply> likesStoryReplies = new HashSet<>();
}


