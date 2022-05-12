package com.todayhouse.domain.story.domain;

import com.todayhouse.domain.image.domain.StoryImage;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.global.common.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Story extends BaseTimeEntity {

    public enum Category {
        STORY, KNOWHOW
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "story_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @CreatedDate
    private LocalDateTime createdDate;


//    기본 게시글 기능 정상 동작 확인 후 상품 클릭 시 링크 연결 기능 추가
//    @Column(name = "product_link")
//    private String productLink;

    @Column(nullable = false)
    private Integer liked;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "story", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<StoryImage> images = new ArrayList<>();


    @Builder
    public Story(String title, String content, Integer liked, Category category, User user) {
        this.title = title;
        this.content = content;
        this.liked = liked;
        this.category = category;
        this.user = user;
    }

    public void update(String title, String content, Category category) {
        this.title = title;
        this.content = content;
        this.category = category;
    }
}