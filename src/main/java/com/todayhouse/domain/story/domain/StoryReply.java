package com.todayhouse.domain.story.domain;

import com.todayhouse.domain.user.domain.User;
import com.todayhouse.global.common.BaseTimeEntity;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StoryReply extends BaseTimeEntity {
    @Id
    @Column(name = "story_reply_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String content;


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

}


