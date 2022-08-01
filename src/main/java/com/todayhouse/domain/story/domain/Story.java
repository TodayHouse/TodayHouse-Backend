package com.todayhouse.domain.story.domain;

import com.todayhouse.domain.image.domain.StoryImage;
import com.todayhouse.domain.likes.domain.LikesStory;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.global.common.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

//    기본 게시글 기능 정상 동작 확인 후 상품 클릭 시 링크 연결 기능 추가
//    @Column(name = "product_link")
//    private String productLink;

    private Integer views = 0;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Category category;

    private Integer floorSpace;

    @Formula(value = "(select count(1) from likes l " +
                    "where l.story_id = story_id)"
    )
    @Basic(fetch = FetchType.EAGER)
    private int likesCount;


    @Enumerated(EnumType.STRING)
    private ResiType resiType;

    @Enumerated(EnumType.STRING)
    private FamilyType familyType;

    @Enumerated(EnumType.STRING)
    private StyleType styleType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "story", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<StoryImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "story", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<StoryReply> storyReplies = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<LikesStory> likesStories = new HashSet<>();
    @Builder
    public Story(String title, String content, Category category, User user, ResiType resiType, Integer floorSpace, FamilyType familyType, StyleType styleType) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.user = user;
        this.resiType = resiType;
        this.floorSpace = floorSpace;
        this.familyType = familyType;
        this.styleType = styleType;
    }

    public void update(String title, String content, Category category) {
        this.title = title;
        this.content = content;
        this.category = category;
    }

    public void increaseView() {
        this.views += 1;
    }

}