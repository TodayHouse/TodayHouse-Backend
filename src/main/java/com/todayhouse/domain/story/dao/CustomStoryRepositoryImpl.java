package com.todayhouse.domain.story.dao;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.todayhouse.domain.story.domain.FamilyType;
import com.todayhouse.domain.story.domain.ResiType;
import com.todayhouse.domain.story.domain.Story;
import com.todayhouse.domain.story.domain.StyleType;
import com.todayhouse.domain.story.dto.reqeust.StorySearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

import static com.todayhouse.domain.story.domain.QStory.story;

public class CustomStoryRepositoryImpl extends QuerydslRepositorySupport
        implements CustomStoryRepository {
    private final JPAQueryFactory queryFactory;

    public CustomStoryRepositoryImpl(EntityManager entityManager) {
        super(Story.class);
        queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public Page<Story> searchCondition(StorySearchRequest request, Pageable pageable) {
        List<Long> ids = getStoryIds(request, pageable);

        if (CollectionUtils.isEmpty(ids)) {
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }

        List<Story> stories = getStories(ids, pageable);

        JPQLQuery<Story> countQuery = from(story)
                .innerJoin(story.user).fetchJoin()
                .where(
                        eqFamilyType(request.getFamilyType()),
                        eqResiType(request.getResiType()),
                        betweenFloorSpace(request.getFloorSpaceMin(), request.getFloorSpaceMax()),
                        eqStyleType(request.getStyleType()),
                        eqCategory(request.getCategory()),
                        containSearch(request.getSearch())
                );

        return PageableExecutionUtils.getPage(stories, pageable, countQuery::fetchCount);
    }

    private List<Long> getStoryIds(StorySearchRequest request, Pageable pageable) {
        JPAQuery<Long> idQuery = queryFactory.select(story.id)
                .from(story)
                .innerJoin(story.user)
                .where(
                        eqFamilyType(request.getFamilyType()),
                        eqResiType(request.getResiType()),
                        betweenFloorSpace(request.getFloorSpaceMin(), request.getFloorSpaceMax()),
                        eqStyleType(request.getStyleType()),
                        eqCategory(request.getCategory()),
                        containSearch(request.getSearch())
                );

        return getQuerydsl().applyPagination(pageable, idQuery).fetch();
    }

    private List<Story> getStories(List<Long> ids, Pageable pageable) {
        JPAQuery<Story> query = queryFactory.selectFrom(story).
                where(story.id.in(ids));

        return getQuerydsl().applySorting(pageable.getSort(), query).fetch();
    }

    private BooleanExpression eqCategory(Story.Category category) {
        if (category == null)
            return null;
        return story.category.eq(category);
    }

    private BooleanExpression eqStyleType(StyleType styleType) {
        if (styleType == null)
            return null;
        return story.styleType.eq(styleType);
    }

    private BooleanExpression eqResiType(ResiType resiType) {
        if (resiType == null)
            return null;
        return story.resiType.eq(resiType);
    }

    private BooleanExpression eqFamilyType(FamilyType familyType) {
        if (familyType == null)
            return null;
        return story.familyType.eq(familyType);
    }

    private BooleanExpression betweenFloorSpace(Integer floorSpaceMin, Integer floorSpaceMax) {
        if (floorSpaceMin == null || floorSpaceMax == null)
            return null;
        return story.floorSpace.between(floorSpaceMin, floorSpaceMax);
    }

    private BooleanExpression containSearch(String search) {
        if (ObjectUtils.isEmpty(search))
            return null;
        return story.user.nickname.contains(search).or(
                story.content.contains(search).or(
                        story.title.contains(search)
                )
        );
    }
}
